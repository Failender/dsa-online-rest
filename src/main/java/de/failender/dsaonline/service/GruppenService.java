package de.failender.dsaonline.service;

import de.failender.dsaonline.data.entity.GruppeEntity;
import de.failender.dsaonline.data.repository.GruppeRepository;
import de.failender.dsaonline.data.repository.UserRepository;
import de.failender.dsaonline.data.service.HeldRepositoryService;
import de.failender.dsaonline.exceptions.HeldNotFoundException;
import de.failender.dsaonline.exceptions.NotAuthenticatedException;
import de.failender.dsaonline.rest.dto.GruppeIncludingHelden;
import de.failender.dsaonline.rest.dto.HeldWithVersion;
import de.failender.dsaonline.security.SecurityUtils;
import de.failender.heldensoftware.api.HeldenApi;
import de.failender.heldensoftware.api.authentication.TokenAuthentication;
import de.failender.heldensoftware.api.requests.GetAllHeldenRequest;
import de.failender.heldensoftware.xml.heldenliste.Held;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class GruppenService {
	private final GruppeRepository gruppeRepository;
	private final HeldRepositoryService heldRepositoryService;
	private final UserRepository userRepository;
	private final HeldenApi heldenApi;
	private final HeldenService heldenService;

	public GruppenService(GruppeRepository gruppeRepository, HeldRepositoryService heldRepositoryService, UserRepository userRepository, HeldenApi heldenApi, HeldenService heldenService) {
		this.gruppeRepository = gruppeRepository;
		this.heldRepositoryService = heldRepositoryService;
		this.userRepository = userRepository;
		this.heldenApi = heldenApi;
		this.heldenService = heldenService;
	}

	public Flux<HeldWithVersion> mapToHeldWithVersion(Flux<Held> heldEntityFlux, boolean publicOnly, boolean showInactive) {
		return filterHeldWithVersion(heldEntityFlux
				.map(held -> {try {
					return this.heldRepositoryService.findHeldWithLatestVersion(held.getHeldenid());
				} catch(NotAuthenticatedException | AccessDeniedException e) {
					return new HeldWithVersion(null, null);
				} catch(HeldNotFoundException e) {
					return new HeldWithVersion(null, null);
				}
				}), publicOnly, showInactive);
	}

	public Flux<HeldWithVersion> filterHeldWithVersion(Flux<HeldWithVersion> heldWithVersionFlux, boolean publicOnly, boolean showInactive) {
		return heldWithVersionFlux.filter(heldWithVersion -> {
			if(heldWithVersion.getHeld() == null) {
				return false;
			}
			if(heldWithVersion.getHeld().isDeleted()) {
				return false;
			}
			if(publicOnly && !heldWithVersion.getHeld().isPublic()) {
				return false;
			}
			if(!showInactive && ! heldWithVersion.getHeld().isActive()) {
				return false;
			}
			return true;
		});
	}


	public Collection<GruppeIncludingHelden> getGruppenIncludingHelden(boolean publicOnly, boolean showInactive) {
		if(!publicOnly) {
			SecurityUtils.checkRight(SecurityUtils.VIEW_ALL);
		}
		List<GruppeEntity> gruppen = gruppeRepository.findAll();
		Map<String, GruppeIncludingHelden> value = gruppen
				.stream()
				.collect(Collectors.toMap(gruppe -> gruppe.getName(), gruppe -> new GruppeIncludingHelden(gruppe.getName(), gruppe.getId(), new ArrayList<>())));
		mapToHeldWithVersion(Flux.fromIterable(userRepository.findAll())
				.filter(user -> user.getToken() != null)
				.flatMap(user -> heldenApi.request(new GetAllHeldenRequest(new TokenAuthentication(user.getToken()))))
				.flatMapIterable(val -> val.getHeld()), publicOnly, showInactive)
				.subscribe(heldWithVersion -> value.get(heldWithVersion.getHeld().getGruppe().getName()).getHelden().add(heldenService.mapToHeldenInfo(heldWithVersion)));
		return value.values();
	}

	public GruppeIncludingHelden getGruppeIncludingHelden(int groupid, boolean publicOnly, boolean showInactive ) {
		if(!publicOnly) {
			SecurityUtils.checkRight(SecurityUtils.VIEW_ALL);
		}
		GruppeEntity gruppe = gruppeRepository.findById(groupid).get();

		GruppeIncludingHelden gruppeIncludingHelden = new GruppeIncludingHelden(gruppe.getName(), gruppe.getId(), new ArrayList<>());
		filterHeldWithVersion(Flux.fromStream(heldRepositoryService.findByGruppeId(groupid)
				.stream()
				.map(held -> heldRepositoryService.findHeldWithLatestVersion(held))), publicOnly, showInactive)
				.subscribe(heldWithVersion -> gruppeIncludingHelden.getHelden().add(heldenService.mapToHeldenInfo(heldWithVersion)));
		return gruppeIncludingHelden;

	}

	public void updateGruppeDatum(int gruppeId, int datum) {
		GruppeEntity gruppe = this.gruppeRepository.findById(gruppeId).get();
		gruppe.setDatum(datum);
		this.gruppeRepository.save(gruppe);
	}

	public void updateGruppeImage(int gruppeid, String image) {
		GruppeEntity gruppe = this.gruppeRepository.findById(gruppeid).get();
		gruppe.setImage(image);
		this.gruppeRepository.save(gruppe);
	}
}
