package de.failender.dsaonline.service;

import de.failender.dsaonline.data.entity.HeldEntity;
import de.failender.dsaonline.data.entity.UserEntity;
import de.failender.dsaonline.data.repository.HeldRepository;
import de.failender.dsaonline.data.repository.UserRepository;
import de.failender.dsaonline.exceptions.HeldNotFoundException;
import de.failender.dsaonline.rest.helden.*;
import de.failender.dsaonline.security.SecurityUtils;
import de.failender.heldensoftware.xml.datenxml.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class HeldenService {

	@Autowired
	private HeldRepository heldRepository;

	@Autowired
	private ApiService apiService;

	@Autowired
	private UserRepository userRepository;

	public List<HeldenInfo> getAllHeldenForCurrentUser() {
		UserEntity user = SecurityUtils.getCurrentUser();
		List<HeldEntity> heldEntities = heldRepository.findByUserIdAndActive(user.getId(), true);
		return heldEntities.stream()
				.map(this::mapToHeldenInfo)
				.collect(Collectors.toList());

	}

	public HeldenInfo mapToHeldenInfo(HeldEntity heldEntity) {
		return new HeldenInfo(heldEntity.getName(), heldEntity.getCreatedDate(), heldEntity.getVersion(), heldEntity.getGruppe().getName(), heldEntity.getId().getId());
	}

	public Daten getHeldenDaten(BigInteger id, int version) {
		Optional<HeldEntity> heldEntityOptional = this.heldRepository.findByIdIdAndIdVersion(id, version);
		if(!heldEntityOptional.isPresent()) {
			log.error("Held with id {} and version {} could not be found", id, version);
			throw new HeldNotFoundException();
		}
		UserEntity user = SecurityUtils.getCurrentUser();
		if(heldEntityOptional.get().getUserId() != user.getId()) {
			SecurityUtils.checkRight(SecurityUtils.VIEW_ALL);
			UserEntity owningUser = this.userRepository.findById(heldEntityOptional.get().getUserId()).get();
			return apiService.getHeldenDaten(id, heldEntityOptional.get().getVersion(), owningUser.getToken());
		} else {
			return apiService.getHeldenDaten(id, heldEntityOptional.get().getVersion());
		}


	}

	public HeldenUnterschied calculateUnterschied(BigInteger heldenid,int from, int to) {
		//If from is bigger then to flip the values
		if(from > to) {
			int tempFrom = from;
			from = to;
			to= tempFrom;
		}
		Optional<HeldEntity> fromHeldOptional = this.heldRepository.findByIdIdAndIdVersion(heldenid, from);
		if(!fromHeldOptional.isPresent()) {
			throw new HeldNotFoundException();
		}
		Optional<HeldEntity> toHeldOptional = this.heldRepository.findByIdIdAndIdVersion(heldenid, to);
		if(!toHeldOptional.isPresent()) {
			throw new HeldNotFoundException();
		}
		return this.calculateUnterschied(fromHeldOptional.get(), toHeldOptional.get());

	}

	private HeldenUnterschied calculateUnterschied(HeldEntity from, HeldEntity to) {
		if(SecurityUtils.getCurrentUser().getId() != from.getUserId()) {
			SecurityUtils.checkRight(SecurityUtils.VIEW_ALL);
			UserEntity userEntity = this.userRepository.findById(from.getUserId()).get();
			Daten fromDaten = this.apiService.getHeldenDaten(from.getId().getId(), from.getVersion(), userEntity.getToken());
			Daten toDaten = this.apiService.getHeldenDaten(to.getId().getId(), to.getVersion(), userEntity.getToken());
			return calculateUnterschied(fromDaten, toDaten);
		} else {
			Daten fromDaten = this.apiService.getHeldenDaten(from.getId().getId(), from.getVersion());
			Daten toDaten = this.apiService.getHeldenDaten(to.getId().getId(), to.getVersion());
			return calculateUnterschied(fromDaten, toDaten);
		}
	}

	private HeldenUnterschied calculateUnterschied(Daten from, Daten to) {
		HeldenUnterschied heldenUnterschied = new HeldenUnterschied(
				calculateTalentUnterschied(from,to),
				calculateZauberUnterschied(from,to),
				calculateEreignisUnterschied(from,to),
				calculateVorteilUnterschied(from,to));
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
		if(from.getEreignisse().getEreignis().size() > to.getEreignisse().getEreignis().size()) {
			log.error("There is a critical error comparing ereignis for {}. from has more events then to.", from.getAngaben().getName());
			return ereignisUnterschiede;

		}
		int lastIndex = from.getEreignisse().getEreignis().size() -1;
		if(from.getEreignisse().getEreignis().get(lastIndex).equals(to.getEreignisse().getEreignis().get(lastIndex))) {

		} else {
			log.error("here is a critical error comparing ereignis for {}. to has a different item at the last index of from");
		}
		for(int i=lastIndex +1 ; i<to.getEreignisse().getEreignis().size(); i++) {
			ereignisUnterschiede.addNeu(to.getEreignisse().getEreignis().get(i));
		}

		return ereignisUnterschiede;
	}

	private <T extends Unterscheidbar> Unterschiede<T> calculateUnterschied (List<T> fromList, List<T> toList) {
		Unterschiede<T> unterschiede = new Unterschiede<>();
		fromList.forEach(
				from -> {
					Optional<T> toOptional = toList.stream().filter(to -> to.getName().equals(from.getName())).findFirst();
					if(toOptional.isPresent()) {
						T to = toOptional.get();
						toList.remove(to);
						unterschiede.addAenderung(new Unterschied(to.getName(), from.getWert().intValue(), to.getWert().intValue()));
					} else {
						//Talent is present in from, but not in to. This hero unlearned.
						unterschiede.addEntfernt(from);

					}
				}
		);
		toList.forEach(toTalent -> unterschiede.addNeu(toTalent));
		return unterschiede;
	}


}
