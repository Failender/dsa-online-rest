package de.failender.dsaonline.service;

import de.failender.dsaonline.data.entity.GruppeEntity;
import de.failender.dsaonline.data.entity.HeldEntity;
import de.failender.dsaonline.data.entity.UserEntity;
import de.failender.dsaonline.data.repository.HeldRepository;
import de.failender.dsaonline.data.repository.UserRepository;
import de.failender.dsaonline.util.DateUtil;
import de.failender.dsaonline.util.VersionFakeService;
import de.failender.heldensoftware.xml.heldenliste.Held;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserHeldenService {

	private final HeldRepository heldRepository;
	private final UserRepository userRepository;
	private final ApiService apiService;
	private final VersionFakeService versionFakeService;
	private final CachingService cachingService;

	public UserHeldenService(HeldRepository heldRepository, UserRepository userRepository, ApiService apiService, VersionFakeService versionFakeService, CachingService cachingService) {
		this.heldRepository = heldRepository;
		this.userRepository = userRepository;
		this.apiService = apiService;
		this.versionFakeService = versionFakeService;
		this.cachingService = cachingService;
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
					this.forceCacheBuildFor(userEntity, heldEntity, heldEntity.getVersion()+1);

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

			heldEntity.setId(new HeldEntity.HeldEntityId());
			heldEntity.setVersion(1);
			heldEntity.setCreatedDate(DateUtil.convert(held.getHeldlastchange()));
			heldEntity.getId().setId(held.getHeldenid());
			heldEntity.setUserId(userEntity.getId());
			log.info("Saving new held {} for user {} with version {}", heldEntity.getName(), userEntity.getName(), heldEntity.getVersion());
			heldRepository.save(heldEntity);
			forceCacheBuildFor(userEntity, heldEntity, 1);
		});
	}
	public void updateHeldenForUserNoCache(UserEntity userEntity) {
		this.apiService.purgeAllHeldenCache(userEntity.getToken());
		this.updateHeldenForUser(userEntity);
	}

	public void updateHeldenForUser(UserEntity userEntity) {
		if(userEntity.getToken() == null) {
			log.error("User with name {} has null token ", userEntity.getName());
			return;
		}
		List<Held> helden =  apiService.getAllHelden(userEntity.getToken());
		this.updateHeldenForUser(userEntity, helden);

	}

	public void fakeHeldenForUser(UserEntity userEntity) {
		if(userEntity.getToken() == null) {
			log.error("User with name {} has null token ", userEntity.getName());
			return;
		}
		List<Held> helden =  apiService.getAllHelden(userEntity.getToken());
		log.info("Faking versions for user " +userEntity.getName());
		this.versionFakeService.fakeVersions(helden.stream().map(held -> held.getHeldenid()).collect(Collectors.toList()));
	}

	private boolean isOnlineVersionOlder(Held xmlHeld, HeldEntity heldEntity) {
		Date lastEditedDate = DateUtil.convert(xmlHeld.getHeldlastchange());
		if(lastEditedDate.getTime() == heldEntity.getCreatedDate().getTime()) {
			return false;
		}
		return lastEditedDate.after(heldEntity.getCreatedDate());
	}

	private void persistHeld(Held xmlHeld, UserEntity user, int version, GruppeEntity gruppeEntity) {
		HeldEntity heldEntity = new HeldEntity();
		heldEntity.setId(new HeldEntity.HeldEntityId());
		heldEntity.setPdfCached(true);
		heldEntity.setCreatedDate(DateUtil.convert(xmlHeld.getHeldlastchange()));
		heldEntity.setUserId(user.getId());
		heldEntity.setName(xmlHeld.getName());
		heldEntity.setVersion(version);
		heldEntity.setActive(true);
		heldEntity.getId().setId(xmlHeld.getHeldenid());
		heldEntity.setGruppe(gruppeEntity);
		this.heldRepository.save(heldEntity);
	}

	//Force to fetch the held once, so its cache gets build. this can run in a separate thread to dont block the main flow
	private void forceCacheBuildFor(UserEntity userEntity, HeldEntity heldEntity, int version) {
		new Thread(() -> {

			apiService.getHeldenDaten(heldEntity.getId().getId(), version, userEntity.getToken());
			if(!cachingService.hasPdfCache(heldEntity.getId().getId(), version)) {
				log.info("Fetching pdf cache for {} version {}", heldEntity.getId().getId(), version);
				cachingService.setHeldenPdfCache(heldEntity.getId().getId(), version, apiService.getPdf(userEntity.getToken(), heldEntity.getId().getId()));
			}

		}).run();

	}
}
