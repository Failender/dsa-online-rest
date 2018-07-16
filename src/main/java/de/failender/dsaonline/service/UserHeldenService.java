package de.failender.dsaonline.service;

import de.failender.dsaonline.data.entity.HeldEntity;
import de.failender.dsaonline.data.entity.UserEntity;
import de.failender.dsaonline.data.entity.VersionEntity;
import de.failender.dsaonline.data.repository.HeldRepository;
import de.failender.dsaonline.data.repository.UserRepository;
import de.failender.dsaonline.util.VersionFakeService;
import de.failender.dsaonline.util.XmlUtil;
import de.failender.heldensoftware.api.HeldenApi;
import de.failender.heldensoftware.api.authentication.TokenAuthentication;
import de.failender.heldensoftware.api.requests.GetAllHeldenRequest;
import de.failender.heldensoftware.api.requests.ReturnHeldDatenWithEreignisseRequest;
import de.failender.heldensoftware.api.requests.ReturnHeldPdfRequest;
import de.failender.heldensoftware.api.requests.ReturnHeldXmlRequest;
import de.failender.heldensoftware.xml.datenxml.Daten;
import de.failender.heldensoftware.xml.datenxml.Ereignis;
import de.failender.heldensoftware.xml.heldenliste.Held;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.math.BigInteger;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class UserHeldenService {

	@Autowired
	private HeldRepositoryService heldRepositoryService;

	private final HeldRepository heldRepository;
	private final UserRepository userRepository;
	private final HeldenApi heldenApi;

	@Autowired
	private VersionFakeService versionFakeService;

	public UserHeldenService(HeldRepository heldRepository, UserRepository userRepository, HeldenApi heldenApi) {
		this.heldRepository = heldRepository;
		this.userRepository = userRepository;
		this.heldenApi = heldenApi;

	}


	public void updateHeldenForUser(UserEntity userEntity, List<Held> helden) {
		log.info("Updating helden for user {}, online found {}", userEntity.getName(), helden.size());

		heldRepository.findByUserIdAndDeleted(userEntity.getId(), false).forEach(heldEntity -> {
			Optional<Held> heldOptional = helden.stream().filter(_held -> _held.getName().equals(heldEntity.getName())).findFirst();
			if (!heldOptional.isPresent()) {
				log.info("Held with Name {} is no longer online, disabling it", heldEntity.getName());
				heldEntity.setDeleted(true);

			} else {
				Held xmlHeld = heldOptional.get();
				helden.remove(xmlHeld);
				VersionEntity versionEntity = heldRepositoryService.findLatestVersion(heldEntity);
				if (isOnlineVersionOlder(xmlHeld, versionEntity.getCreatedDate())) {
					log.info("Got a new version for held with getName {}", heldEntity.getName());
					//We got a new version of this xmlHeld
					String xml = heldenApi.request(new ReturnHeldXmlRequest(xmlHeld.getHeldenid(), new TokenAuthentication(userEntity.getToken()), versionEntity.getVersion() +1)).block();
					this.persistVersion(xmlHeld.getHeldenid(), userEntity, versionEntity.getVersion() +1, xml);
					this.forceCacheBuildFor(userEntity, heldEntity, versionEntity.getVersion() + 1);

				} else {
					log.info("Held with Name {} is already on latest version", heldEntity.getName());
				}
			}
		});
		helden.forEach(held -> {
			ReturnHeldXmlRequest returnHeldXmlRequest = new ReturnHeldXmlRequest(held.getHeldenid(), new TokenAuthentication(userEntity.getToken()), 1);
			String xml = heldenApi.request(returnHeldXmlRequest).block();
			HeldEntity heldEntity = new HeldEntity();
			heldEntity.setGruppe(userEntity.getGruppe());
			heldEntity.setName(held.getName());
			heldEntity.setCreatedDate(new Date());
			heldEntity.setId(held.getHeldenid());
			heldEntity.setUserId(userEntity.getId());
			heldRepositoryService.saveHeld(heldEntity);
			persistVersion(held.getHeldenid(), userEntity, 1, xml);
			log.info("Saving new held {} for user {} with version {}", heldEntity.getName(), userEntity.getName(), 1);
			forceCacheBuildFor(userEntity, heldEntity, 1);
		});
	}

	public void updateHeldenForToken(String token) {
		updateHeldenForUser(userRepository.findByToken(token), true);
	}

	public void updateHeldenForUser(UserEntity userEntity, boolean cache) {
		if (userEntity.getToken() == null) {
			log.error("User with getName {} has null token ", userEntity.getName());
			return;
		}
		List<Held> helden = heldenApi.request(new GetAllHeldenRequest(new TokenAuthentication(userEntity.getToken())), cache).block().getHeld();
		this.updateHeldenForUser(userEntity, helden);

	}

	private boolean isOnlineVersionOlder(Held xmlHeld, Date heldCreatedDate) {


		Date lastEditedDate = new Date((xmlHeld.getHeldlastchange().longValue() / 1000L) * 1000L);
		if (lastEditedDate.getTime() == heldCreatedDate.getTime()) {
			return false;
		}
		return lastEditedDate.after(heldCreatedDate);
	}

	public void persistVersion(BigInteger heldid, UserEntity user, int version, String xml) {
		Date date = XmlUtil.getStandFromString(xml);
		VersionEntity versionEntity = new VersionEntity();
		versionEntity.setVersion(version);
		versionEntity.setHeldid(heldid);
		Daten daten = heldenApi.request(new ReturnHeldDatenWithEreignisseRequest(heldid, new TokenAuthentication(user.getToken()), version)).block();
		versionEntity.setCreatedDate(date);
		versionEntity.setLastEvent(extractLastEreignisString(daten.getEreignisse().getEreignis()));
		versionEntity.setAp(daten.getAngaben().getAp().getGesamt().intValue());
		this.heldRepositoryService.saveVersion(versionEntity);
	}

	private void forceCacheBuildFor(UserEntity userEntity, HeldEntity heldEntity, int version) {
		Mono.zip(heldenApi.request(new ReturnHeldXmlRequest(heldEntity.getId(), new TokenAuthentication(userEntity.getToken()), version), false),
				heldenApi.request(new ReturnHeldPdfRequest(heldEntity.getId(), new TokenAuthentication(userEntity.getToken()), version), false))
				.subscribe(data -> IOUtils.closeQuietly(data.getT2()));
	}

	public void forceUpdateHeldenForUser(UserEntity userEntity) {
		log.info("Refreshing helden for user {}", userEntity.getName());
		if (userEntity.getToken() != null) {
			this.updateHeldenForUser(userEntity, false);
		}

	}

	public static String extractLastEreignisString(List<Ereignis> ereignisse) {
		Ereignis ereignis = extractLastEreignis(ereignisse);
		if(ereignis == null) {
			return null;
		}
		String s = ereignis.getKommentar();
		int index = s.indexOf("Gesamt AP");
		if (index == -1) {
			return s;
		}
		s = s.substring(0, index);

		return s;
	}

	public static Ereignis extractLastEreignis(List<Ereignis> ereignisse) {
		for (int i = ereignisse.size() - 1; i >= 0; i--) {
			if (ereignisse.get(i).getAp() > 0) {
				return ereignisse.get(i);
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
}
