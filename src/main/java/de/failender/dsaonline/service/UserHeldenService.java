package de.failender.dsaonline.service;

import de.failender.dsaonline.data.entity.HeldEntity;
import de.failender.dsaonline.data.entity.UserEntity;
import de.failender.dsaonline.data.entity.VersionEntity;
import de.failender.dsaonline.data.repository.HeldRepository;
import de.failender.dsaonline.data.repository.UserRepository;
import de.failender.dsaonline.util.DateUtil;
import de.failender.dsaonline.util.VersionFakeService;
import de.failender.heldensoftware.xml.datenxml.Ereignis;
import de.failender.heldensoftware.xml.heldenliste.Held;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserHeldenService {

	@Autowired
	private HeldRepositoryService heldRepositoryService;

	private final HeldRepository heldRepository;
	private final UserRepository userRepository;
	private ApiService apiService;
	private final VersionFakeService versionFakeService;
	private final CachingService cachingService;

	public UserHeldenService(HeldRepository heldRepository, UserRepository userRepository, VersionFakeService versionFakeService, CachingService cachingService) {
		this.heldRepository = heldRepository;
		this.userRepository = userRepository;
		this.versionFakeService = versionFakeService;
		this.cachingService = cachingService;
	}


	public void updateHeldenForUser(UserEntity userEntity, List<Held> helden) {
		log.info("Updating helden for user {}, online found {}", userEntity.getName(), helden.size());

		heldRepository.findByUserIdAndDeleted(userEntity.getId(), false).forEach(heldEntity -> {
			Optional<Held> heldOptional = helden.stream().filter(_held -> _held.getName().equals(heldEntity.getName())).findFirst();
			if (!heldOptional.isPresent()) {
				log.info("Held with name {} is no longer online, disabling it", heldEntity.getName());
				heldEntity.setDeleted(true);

			} else {
				Held xmlHeld = heldOptional.get();
				helden.remove(xmlHeld);
				VersionEntity versionEntity = heldRepositoryService.findLatestVersion(heldEntity);
				if (isOnlineVersionOlder(xmlHeld, versionEntity.getCreatedDate())) {
					log.info("Got a new version for held with name {}", heldEntity.getName());
					//We got a new version of this xmlHeld
					this.persistVersion(xmlHeld, userEntity, versionEntity.getId().getVersion() + 1);
					this.forceCacheBuildFor(userEntity, heldEntity, versionEntity.getId().getVersion() + 1);

				} else {
					log.info("Held with name {} is already on latest version", heldEntity.getName());
				}
			}
		});
		helden.forEach(held -> {
			HeldEntity heldEntity = new HeldEntity();
			heldEntity.setGruppe(userEntity.getGruppe());
			heldEntity.setName(held.getName());
			heldEntity.setCreatedDate(new Date());
			heldEntity.setId(held.getHeldenid());
			heldEntity.setUserId(userEntity.getId());
			heldRepositoryService.saveHeld(heldEntity);

			VersionEntity versionEntity = new VersionEntity();
			versionEntity.setId(new VersionEntity.VersionId(held.getHeldenid(), 1));
			versionEntity.setCreatedDate(DateUtil.convert(held.getHeldlastchange()));
			heldRepositoryService.saveVersion(versionEntity);
			log.info("Saving new held {} for user {} with version {}", heldEntity.getName(), userEntity.getName(), versionEntity.getId().getVersion());
			forceCacheBuildFor(userEntity, heldEntity, 1);
		});
	}

	public void updateHeldenForToken(String token) {
		updateHeldenForUser(userRepository.findByToken(token));
	}

	public void updateHeldenForUser(UserEntity userEntity) {
		if (userEntity.getToken() == null) {
			log.error("User with name {} has null token ", userEntity.getName());
			return;
		}
		List<Held> helden = apiService.getAllHelden(userEntity.getToken());
		this.updateHeldenForUser(userEntity, helden);

	}

	public void fakeHeldenForUser(UserEntity userEntity) {
		if (userEntity.getToken() == null) {
			log.error("User with name {} has null token ", userEntity.getName());
			return;
		}
		List<Held> helden = apiService.getAllHelden(userEntity.getToken());
		log.info("Faking versions for user " + userEntity.getName());
		this.versionFakeService.fakeVersions(helden.stream().map(held -> held.getHeldenid()).collect(Collectors.toList()));
	}

	private boolean isOnlineVersionOlder(Held xmlHeld, Date heldCreatedDate) {


		Date lastEditedDate = new Date((xmlHeld.getHeldlastchange().longValue() / 1000L) * 1000L);
		if (lastEditedDate.getTime() == heldCreatedDate.getTime()) {
			return false;
		}
		return lastEditedDate.after(heldCreatedDate);
	}

	private void persistVersion(Held xmlHeld, UserEntity user, int version) {

		VersionEntity versionEntity = new VersionEntity();
		versionEntity.setId(new VersionEntity.VersionId(xmlHeld.getHeldenid(), version));
		versionEntity.setCreatedDate(DateUtil.convert(xmlHeld.getHeldlastchange()));

		versionEntity.setLastEvent(extractLastEreignis(apiService.getHeldenDaten(xmlHeld.getHeldenid(), version, user.getToken()).getEreignisse().getEreignis()));

		this.heldRepositoryService.saveVersion(versionEntity);
	}

	//Force to fetch the held once, so its cache gets build. this can run in a separate thread to dont block the main flow
	private void forceCacheBuildFor(UserEntity userEntity, HeldEntity heldEntity, int version) {
		new Thread(() -> {

			apiService.getHeldenDaten(heldEntity.getId(), version, userEntity.getToken());
			if (!cachingService.hasPdfCache(heldEntity.getId(), version)) {
				log.info("Fetching pdf cache for {} version {}", heldEntity.getId(), version);
				cachingService.setHeldenPdfCache(heldEntity.getId(), version, apiService.getPdf(userEntity.getToken(), heldEntity.getId()));
			}

		}).run();

	}

	public void forceUpdateHeldenForUser(UserEntity userEntity) {
		log.info("Refreshing helden for user {}", userEntity.getName());
		if (userEntity.getToken() != null) {
			this.apiService.purgeAllHeldenCache(userEntity.getToken());
			this.updateHeldenForUser(userEntity);
		}

	}

	public static String extractLastEreignis(List<Ereignis> ereignisse) {
		for(int i= ereignisse.size() -1 ; i>= 0 ; i--) {
			if(ereignisse.get(i).getAp() > 0) {
				String s  = ereignisse.get(i).getKommentar();
				int index = s.indexOf("Gesamt AP");
				if(index == -1) {
					return s;
				}
				s = s.substring(0, index);

				return s;
			}
		}
		return null;
	}

	public static void clearEreigniskontrolle(List<Ereignis> ereignisse) {

		Ereignis ereignis = ereignisse.get(ereignisse.size() - 1);
		if (ereignis.getAktion().equals("Änderungskontrolle")) {
			ereignisse.remove(ereignisse.size() - 1);
		}
	}

	@Autowired
	public void setApiService(ApiService apiService) {
		this.apiService = apiService;
	}
}
