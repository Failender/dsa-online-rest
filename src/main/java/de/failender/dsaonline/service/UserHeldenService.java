package de.failender.dsaonline.service;

import de.failender.dsaonline.data.entity.GruppeEntity;
import de.failender.dsaonline.data.entity.HeldEntity;
import de.failender.dsaonline.data.entity.UserEntity;
import de.failender.dsaonline.data.repository.HeldRepository;
import de.failender.dsaonline.util.DateUtil;
import de.failender.heldensoftware.xml.heldenliste.Held;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class UserHeldenService {

	private final HeldRepository heldRepository;

	private final ApiService apiService;

	public UserHeldenService(HeldRepository heldRepository, ApiService apiService) {
		this.heldRepository = heldRepository;
		this.apiService = apiService;
	}


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
					this.persistHeld(heldOptional.get(), userEntity, heldEntity.getVersion()+1, heldEntity.getGruppe());
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
			heldEntity.setId(new HeldEntity.HeldEntityId());
			heldEntity.getId().setCreatedDate(DateUtil.convert(held.getHeldlastchange()));
			heldEntity.getId().setId(held.getHeldenid());
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
		System.out.println("DATES");
		System.out.println(lastEditedDate.getTime());
		System.out.println(heldEntity.getId().getCreatedDate().getTime());
		System.out.println(lastEditedDate.after(heldEntity.getId().getCreatedDate()));
		return lastEditedDate.after(heldEntity.getId().getCreatedDate());
	}

	private void persistHeld(Held xmlHeld, UserEntity user, int version, GruppeEntity gruppeEntity) {
		HeldEntity heldEntity = new HeldEntity();
		heldEntity.setId(new HeldEntity.HeldEntityId());
		heldEntity.getId().setCreatedDate(DateUtil.convert(xmlHeld.getHeldlastchange()));
		heldEntity.setUserId(user.getId());
		heldEntity.setName(xmlHeld.getName());
		heldEntity.setVersion(version);
		heldEntity.getId().setId(xmlHeld.getHeldenid());
		heldEntity.setGruppe(gruppeEntity);
		this.heldRepository.save(heldEntity);
	}
}
