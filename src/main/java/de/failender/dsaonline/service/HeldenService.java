package de.failender.dsaonline.service;

import de.failender.dsaonline.data.entity.HeldEntity;
import de.failender.dsaonline.data.entity.UserEntity;
import de.failender.dsaonline.data.entity.VersionEntity;
import de.failender.dsaonline.data.repository.UserRepository;
import de.failender.dsaonline.data.repository.VersionRepository;
import de.failender.dsaonline.rest.helden.*;
import de.failender.dsaonline.security.SecurityUtils;
import de.failender.heldensoftware.xml.datenxml.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.math.BigInteger;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class HeldenService {

	@Autowired
	private HeldRepositoryService heldRepositoryService;

	@Autowired
	private ApiService apiService;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private CachingService cachingService;

	@Autowired
	private VersionRepository versionRepository;

	public List<HeldenInfo> getAllHeldenForCurrentUser() {
		UserEntity user = SecurityUtils.getCurrentUser();
		return heldRepositoryService.findByUserId(user.getId())
				.stream()
				.map(held -> heldRepositoryService.findHeldWithLatestVersion(held))
				.map(this::mapToHeldenInfo)
				.collect(Collectors.toList());
	}

	public HeldenInfo mapToHeldenInfo(HeldWithVersion heldWithVersion) {
		return new HeldenInfo(heldWithVersion.getHeld().getName(),
				heldWithVersion.getVersion().getCreatedDate(),
				heldWithVersion.getVersion().getId().getVersion(),
				heldWithVersion.getHeld().getGruppe().getName(),
				heldWithVersion.getHeld().getId(),
				heldWithVersion.getHeld().isPublic());
	}

	public Daten getHeldenDaten(BigInteger id, int version) {
		HeldEntity held = heldRepositoryService.findHeld(id);
		//Throw error if version not present
		heldRepositoryService.findVersion(id, version);

		SecurityUtils.canCurrentUserViewHeld(held);
		UserEntity owningUser = this.userRepository.findById(held.getUserId()).get();
		return apiService.getHeldenDaten(id, version, owningUser.getToken());
	}

	public HeldenUnterschied calculateUnterschied(BigInteger heldenid, int from, int to) {
		//If from is bigger then to flip the values
		if (from > to) {
			int tempFrom = from;
			from = to;
			to = tempFrom;
		}
		HeldEntity held = heldRepositoryService.findHeld(heldenid);
		VersionEntity fromVersion = heldRepositoryService.findVersion(heldenid, from);
		VersionEntity toVersion = heldRepositoryService.findVersion(heldenid, to);
		return this.calculateUnterschied(held, fromVersion, toVersion);

	}

	private HeldenUnterschied calculateUnterschied(HeldEntity held, VersionEntity from, VersionEntity to) {
		String token;
		SecurityUtils.canCurrentUserViewHeld(held);
		UserEntity userEntity = this.userRepository.findById(held.getUserId()).get();
		token = userEntity.getToken();
		Daten fromDaten = this.apiService.getHeldenDaten(held.getId(), from.getId().getVersion(), token);
		Daten toDaten = this.apiService.getHeldenDaten(held.getId(), to.getId().getVersion(), token);
		return calculateUnterschied(fromDaten, toDaten);
	}

	private HeldenUnterschied calculateUnterschied(Daten from, Daten to) {
		HeldenUnterschied heldenUnterschied = new HeldenUnterschied(
				calculateTalentUnterschied(from, to),
				calculateZauberUnterschied(from, to),
				calculateEreignisUnterschied(from, to),
				calculateVorteilUnterschied(from, to));
		return heldenUnterschied;
	}

	private Unterschiede<Talent> calculateTalentUnterschied(Daten from, Daten to) {
		return calculateUnterschied(from.getTalentliste().getTalent(), to.getTalentliste().getTalent());
	}

	private Unterschiede<Zauber> calculateZauberUnterschied(Daten from, Daten to) {
		return calculateUnterschied(from.getZauberliste().getZauber(), to.getZauberliste().getZauber());
	}

	private Unterschiede<Vorteil> calculateVorteilUnterschied(Daten from, Daten to) {
		return calculateUnterschied(from.getVorteile().getVorteil(), to.getVorteile().getVorteil());
	}

	//Calculating ereignis unterschied only supports showing all events after the last one in from
	private Unterschiede<Ereignis> calculateEreignisUnterschied(Daten from, Daten to) {
		Unterschiede<Ereignis> ereignisUnterschiede = new Unterschiede<>();
		if (from.getEreignisse().getEreignis().size() > to.getEreignisse().getEreignis().size()) {
			log.error("There is a critical error comparing ereignis for {}. from has more events then to.", from.getAngaben().getName());
			return ereignisUnterschiede;

		}
		int lastIndex = from.getEreignisse().getEreignis().size() - 1;
		if (from.getEreignisse().getEreignis().get(lastIndex).equals(to.getEreignisse().getEreignis().get(lastIndex))) {

		} else {
			log.error("here is a critical error comparing ereignis for {}. to has a different item at the last index of from");
		}
		for (int i = lastIndex + 1; i < to.getEreignisse().getEreignis().size(); i++) {
			ereignisUnterschiede.addNeu(to.getEreignisse().getEreignis().get(i));
		}

		return ereignisUnterschiede;
	}

	private <T extends Unterscheidbar> Unterschiede<T> calculateUnterschied(List<T> fromList, List<T> toList) {
		Unterschiede<T> unterschiede = new Unterschiede<>();
		fromList.forEach(
				from -> {
					Optional<T> toOptional = toList.stream().filter(to -> to.getName().equals(from.getName())).findFirst();
					if (toOptional.isPresent()) {
						T to = toOptional.get();
						toList.remove(to);
						Integer fromWert = from.getWert();
						Integer toWert = to.getWert();
						if(fromWert == null && toWert == null
								|| fromWert != null && toWert != null && fromWert.equals(toWert)) {
							return;
						}
						unterschiede.addAenderung(new Unterschied(to.getName(), from.getWert(), to.getWert()));

					} else {
						//Talent is present in from, but not in to. This hero unlearned.
						unterschiede.addEntfernt(from);

					}
				}
		);
		toList.forEach(toTalent -> unterschiede.addNeu(toTalent));
		return unterschiede;
	}

	public List<HeldVersion> loadHeldenVersionen(BigInteger heldenid) {
		List<VersionEntity> versionen = versionRepository.findByIdHeldid(heldenid);
		return versionen
				.stream()
				.map(version -> new HeldVersion(version.getLastEvent(), version.getCreatedDate(), version.getId().getVersion()))
				.collect(Collectors.toList());
	}

	public void providePdfDownload(BigInteger id, int version, HttpServletResponse response) {
		HeldEntity held = heldRepositoryService.findHeld(id);
		SecurityUtils.canCurrentUserViewHeld(held);
		heldRepositoryService.findVersion(id, version);

		cachingService.provideDownload(id, version, response, CachingService.CacheType.pdf);
	}

	public void updateHeldenPublic(boolean isPublic, BigInteger heldid) {
		HeldEntity held = heldRepositoryService.findHeld(heldid);
		log.info("Updating public status for held {}: {}", held.getName(), isPublic);
		SecurityUtils.canCurrentUserEditHeld(held);
		heldRepositoryService.updateHeldenPublic(isPublic, heldid);
	}


}
