package de.failender.dsaonline.service;

import de.failender.dsaonline.data.entity.HeldEntity;
import de.failender.dsaonline.data.entity.UserEntity;
import de.failender.dsaonline.data.entity.VersionEntity;
import de.failender.dsaonline.data.repository.UserRepository;
import de.failender.dsaonline.data.repository.VersionRepository;
import de.failender.dsaonline.rest.helden.*;
import de.failender.dsaonline.security.SecurityUtils;
import de.failender.heldensoftware.api.HeldenApi;
import de.failender.heldensoftware.api.authentication.TokenAuthentication;
import de.failender.heldensoftware.api.requests.ReturnHeldDatenWithEreignisseRequest;
import de.failender.heldensoftware.api.requests.ReturnHeldPdfRequest;
import de.failender.heldensoftware.api.requests.ReturnHeldXmlRequest;
import de.failender.heldensoftware.xml.datenxml.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.util.function.Tuple2;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigInteger;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static de.failender.dsaonline.security.SecurityUtils.getAuthentication;

@Service
@Slf4j
public class HeldenService {

	@Autowired
	private HeldRepositoryService heldRepositoryService;

	@Autowired
	private HeldenApi heldenApi;

	@Autowired
	private UserRepository userRepository;

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

	public Daten findHeldWithLatestVersion(BigInteger id) {
		HeldWithVersion heldWithVersion = heldRepositoryService.findHeldWithLatestVersion(id);
		return getHeldenDaten(id, heldWithVersion.getVersion().getVersion());
	}

	public HeldenInfo mapToHeldenInfo(HeldWithVersion heldWithVersion) {
		return new HeldenInfo(heldWithVersion.getHeld().getName(),
				heldWithVersion.getVersion().getCreatedDate(),
				heldWithVersion.getVersion().getVersion(),
				heldWithVersion.getHeld().getGruppe().getName(),
				heldWithVersion.getHeld().getId(),
				heldWithVersion.getHeld().isPublic(),
				heldWithVersion.getHeld().isActive());
	}

	public Daten getHeldenDaten(BigInteger id, int version) {
		HeldEntity held = heldRepositoryService.findHeld(id);

		//Throw error if version not present
		heldRepositoryService.findVersion(id, version);
		UserEntity owningUser = this.userRepository.findById(held.getUserId()).get();
		return heldenApi.request(new ReturnHeldDatenWithEreignisseRequest(id, new TokenAuthentication(owningUser.getToken()), version), true).block();
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
		UserEntity userEntity = this.userRepository.findById(held.getUserId()).get();
		String token = userEntity.getToken();
		Tuple2<Daten, Daten> datenTuple = heldenApi.request(new ReturnHeldDatenWithEreignisseRequest(held.getId(), new TokenAuthentication(token), from.getVersion())).zipWith(
				heldenApi.request(new ReturnHeldDatenWithEreignisseRequest(held.getId(), new TokenAuthentication(token), to.getVersion()))).block();
		return calculateUnterschied(datenTuple.getT1(), datenTuple.getT2());
	}

	public HeldenUnterschied calculateUnterschied(Daten from, Daten to) {
		HeldenUnterschied heldenUnterschied = new HeldenUnterschied(
				calculateTalentUnterschied(from, to),
				calculateZauberUnterschied(from, to),
				calculateEreignisUnterschied(from, to),
				calculateVorteilUnterschied(from, to),
				calculateGegenstandUnterschied(from, to));
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

	private Unterschiede<Gegenstand> calculateGegenstandUnterschied(Daten from, Daten to) {
		Unterschiede<Gegenstand> unterschiede = new Unterschiede<>();
		List<Gegenstand> toGegenstaende = to.getGegenstaende().getGegenstand();
		for (Gegenstand gegenstand : from.getGegenstaende().getGegenstand()) {
			Optional<Gegenstand> toGegenstandOpt = toGegenstaende.stream()
					.filter(gegenstand1 -> gegenstand1.getName().equals(gegenstand.getName()))
					.findFirst();
			if(toGegenstandOpt.isPresent()) {
				toGegenstaende.remove(toGegenstandOpt.get());
			} else {
				unterschiede.addEntfernt(gegenstand);
			}
		}
		for (Gegenstand gegenstand : toGegenstaende) {
			unterschiede.addNeu(gegenstand);
		}
		return unterschiede;
	}

	//Calculating ereignis unterschied only supports showing all events after the last one in from
	private Unterschiede<Ereignis> calculateEreignisUnterschied(Daten from, Daten to) {
		Unterschiede<Ereignis> ereignisUnterschiede = new Unterschiede<>();
		if (from.getEreignisse().getEreignis().size() > to.getEreignisse().getEreignis().size()) {
			log.error("There is a critical error comparing ereignis for {}. from has more events then to.", from.getAngaben().getName());
//			return ereignisUnterschiede; Switch both around and compare that way. TODO Maybe use a processing heaviver approach if this is happening
			Daten temp
					= from;
			from = to;
			to = temp;
		}
		int lastIndex = from.getEreignisse().getEreignis().size() - 1;
		if (from.getEreignisse().getEreignis().get(lastIndex).equals(to.getEreignisse().getEreignis().get(lastIndex))) {

		} else {
			log.error("here is a critical error comparing ereignis for {}. to has a different item at the last index of from", from.getAngaben().getName());
			log.error(from.getEreignisse().getEreignis().get(lastIndex).toString());
			log.error(to.getEreignisse().getEreignis().get(lastIndex).toString());
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
						if (fromWert == null && toWert == null
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
		List<VersionEntity> versionen = versionRepository.findByHeldidOrderByVersionDesc(heldenid);
		return versionen
				.stream()
				.map(version -> new HeldVersion(version.getLastEvent(), version.getCreatedDate(), version.getVersion()))
				.collect(Collectors.toList());
	}

	public void providePdfDownload(BigInteger id, int version, HttpServletResponse response) {
		HeldEntity held = heldRepositoryService.findHeld(id);
		heldRepositoryService.findVersion(id, version);
		heldenApi.provideDownload(new ReturnHeldPdfRequest(id, getAuthentication(), version), response);
	}

	public void updateHeldenPublic(boolean isPublic, BigInteger heldid) {
		HeldEntity held = heldRepositoryService.findHeld(heldid);
		SecurityUtils.canCurrentUserEditHeld(held);
		log.info("Updating public status for held {}: {}", held.getName(), isPublic);
		heldRepositoryService.updateHeldenPublic(isPublic, heldid);
	}

	public void updateHeldenActive(boolean isActive, BigInteger heldid) {
		HeldEntity held = heldRepositoryService.findHeld(heldid);
		SecurityUtils.canCurrentUserEditHeld(held);
		log.info("Updating active status for held {}: {}", held.getName(), isActive);
		heldRepositoryService.updateHeldenActive(isActive, heldid);
	}

	public List<Daten> findPublicByGruppeId(Integer gruppeId) {
		return heldRepositoryService.findByGruppeId(gruppeId)
				.stream()
				.filter(HeldEntity::isPublic)
				.map(HeldEntity::getId)
				.map(this::findHeldWithLatestVersion)
				.collect(Collectors.toList());
	}

	public List<Daten> findAllByGruppeId(Integer gruppeId) {
		SecurityUtils.checkRight(SecurityUtils.VIEW_ALL);
		return heldRepositoryService.findByGruppeId(gruppeId)
				.stream()
				.map(HeldEntity::getId)
				.map(this::findHeldWithLatestVersion)
				.collect(Collectors.toList());
	}

	public List<BigInteger> getAllHeldenIds() {
		return heldRepositoryService.getAllHeldenIds();
	}


	public void provideXmlDownload(BigInteger heldid, HttpServletResponse response) {
		response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
		response.setHeader("Content-disposition", "attachment; filename=versionen.zip");
		ZipOutputStream zos;
		try {
			zos = new ZipOutputStream(response.getOutputStream());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		List<VersionEntity> versions = versionRepository.findByHeldid(heldid);
		Flux.fromIterable(versions)
				.flatMap(version ->heldenApi.requestRaw(new ReturnHeldXmlRequest(heldid, null, version.getVersion()), true))
				.zipWith(Flux.range(1, versions.size()))
				.doOnNext(tuple -> {
					try {
						zos.putNextEntry(new ZipEntry(tuple.getT2() + ".xml"));
						org.apache.commons.io.IOUtils.copy(tuple.getT1(), zos);
						tuple.getT1().close();
						zos.closeEntry();
					} catch (IOException e) {
						e.printStackTrace();
					}
				})
				.blockLast();
		org.apache.commons.io.IOUtils.closeQuietly(zos);

	}
}
