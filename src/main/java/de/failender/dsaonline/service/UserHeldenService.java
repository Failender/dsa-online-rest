package de.failender.dsaonline.service;

import de.failender.dsaonline.data.entity.HeldEntity;
import de.failender.dsaonline.data.entity.UserEntity;
import de.failender.dsaonline.data.repository.HeldRepository;
import de.failender.dsaonline.util.DateUtil;
import de.failender.heldensoftware.xml.heldenliste.Held;
import org.flywaydb.core.internal.util.DateUtils;
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


	public void updateHeldenForUser(UserEntity userEntity, List<Held> helden) {
		List<HeldEntity> heldEntities = heldRepository.findByUserIdAndActive(userEntity.getId(), true);
		heldEntities.forEach(heldEntity -> {
			Optional<Held> heldOptional = helden.stream().filter(_held -> _held.getName().equals(heldEntity.getName())).findFirst();
			if(!heldOptional.isPresent()) {
				heldEntity.setActive(false);

			} else {
				helden.remove(heldOptional.get());
				if(isOnlineVersionOlder(heldOptional.get(), heldEntity)) {
					//We got a new version of this xmlHeld
					heldEntity.setActive(false);
					this.heldRepository.save(heldEntity);
					this.persistHeld(heldOptional.get(), userEntity, heldEntity.getVersion()+1);
				}
			}
		});
		helden.forEach(held -> {
			HeldEntity heldEntity = new HeldEntity();
			heldEntity.setActive(true);
			heldEntity.setGruppe(userEntity.getGruppe());
			heldEntity.setName(held.getName());
			heldEntity.setVersion(1);
			heldEntity.setCreatedDate(new Date());
			heldEntity.setId(held.getHeldenid());
			heldEntity.setUserId(userEntity.getId());
			heldRepository.save(heldEntity);
		});
	}

	public void updateHeldenForUser(UserEntity userEntity) {
		List<Held> helden =  apiService.getAllHelden();
		this.updateHeldenForUser(userEntity, helden);

	}

	private boolean isOnlineVersionOlder(Held xmlHeld, HeldEntity heldEntity) {
		Date lastEditedDate = DateUtil.convert(xmlHeld.getHeldlastchange());
		return lastEditedDate.after(heldEntity.getCreatedDate());
	}

	private void persistHeld(Held xmlHeld, UserEntity user, int version) {
		HeldEntity heldEntity = new HeldEntity();
		heldEntity.setCreatedDate(DateUtil.convert(xmlHeld.getHeldlastchange()));
		heldEntity.setUserId(user.getId());
		heldEntity.setName(xmlHeld.getName());
		heldEntity.setVersion(version);
		this.heldRepository.save(heldEntity);
	}
}
