package de.failender.dsaonline.service;

import de.failender.dsaonline.data.entity.HeldEntity;
import de.failender.dsaonline.data.entity.UserEntity;
import de.failender.dsaonline.data.repository.HeldRepository;
import de.failender.heldensoftware.xml.heldenliste.Held;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class UserHeldenService {


	@Autowired
	private HeldRepository heldRepository;

	@Autowired
	private ApiService apiService;


	public void updateHeldenForUser(UserEntity userEntity) {
		List<Held> helden =  apiService.getAllHelden();
		List<HeldEntity> heldEntities = heldRepository.findByUserId(userEntity.getId());
		heldEntities.forEach(heldEntity -> {
			Optional<Held> heldOptional = helden.stream().filter(_held -> _held.getName().equals(heldEntity.getName())).findFirst();
			if(!heldOptional.isPresent()) {
				heldEntity.setActive(false);
			} else {
				if(isOnlineVersionOlder(heldOptional.get(), heldEntity)) {
					//We got a new version of this held
					heldEntity.setActive(false);
					this.persistHeld(heldOptional.get(), userEntity);
				}
			}
		});
		helden.forEach(held -> {
			HeldEntity heldEntity = new HeldEntity();
			heldEntity.setActive(true);
			heldEntity.setGruppe(userEntity.getGruppe());
			heldEntity.setName(held.getName());
			heldEntity.setXml(apiService.getHeldXml(held.getHeldenid()));
		});

	}

	private boolean isOnlineVersionOlder(Held held, HeldEntity heldEntity) {
		Date lastEditedDate = new Date(held.getHeldlastchange().divide(BigInteger.valueOf(10L)).longValue());
		return lastEditedDate.after(heldEntity.getCreatedDate());
	}

	private void persistHeld(Held held, UserEntity user) {
		HeldEntity heldEntity = new HeldEntity();
		heldEntity.setCreatedDate(new Date());
		heldEntity.setUserId(user.getId());
		heldEntity.setName(held.getName());
	}
}
