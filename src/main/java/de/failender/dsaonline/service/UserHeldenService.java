package de.failender.dsaonline.service;

import de.failender.dsaonline.data.entity.HeldEntity;
import de.failender.dsaonline.data.entity.UserEntity;
import de.failender.dsaonline.data.repository.HeldRepository;
import de.failender.dsaonline.util.DateUtil;
import de.failender.heldensoftware.xml.heldenliste.Held;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class UserHeldenService {


	@Autowired
	private HeldRepository heldRepository;

	@Autowired
	private ApiService apiService;


	public void updateHeldenForUser(UserEntity userEntity, List<Held> helden) {
		log.info("Updating helden for user {}, online found {}", userEntity.getName(), helden.size());
		List<HeldEntity> heldEntities = heldRepository.findByUserIdAndActive(userEntity.getId(), true);
		heldEntities.forEach(heldEntity -> {
			Optional<Held> heldOptional = helden.stream().filter(_held -> _held.getName().equals(heldEntity.getName())).findFirst();
			if(!heldOptional.isPresent()) {
				log.info("Held with name {} is no longer online, disabling it", heldEntity.getName());
				heldEntity.setActive(false);

			} else {
				helden.remove(heldOptional.get());
				if(isOnlineVersionOlder(heldOptional.get(), heldEntity)) {
					log.info("Got a new version for held with name {}", heldEntity.getName());
					//We got a new version of this xmlHeld
					heldEntity.setActive(false);
					this.heldRepository.save(heldEntity);
					this.persistHeld(heldOptional.get(), userEntity, heldEntity.getVersion()+1);
				} else {
					log.info("Held with name {} is already on latest version", heldEntity.getName());
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
			log.info("Saving new held {} for user {}", heldEntity.getName(), userEntity.getName());
			heldRepository.save(heldEntity);
		});
	}

	public void updateHeldenForUser(UserEntity userEntity) {

		List<Held> helden =  apiService.getAllHelden(userEntity.getToken());
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
