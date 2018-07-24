package de.failender.dsaonline.rest.gruppen;

import de.failender.dsaonline.data.entity.GruppeEntity;
import de.failender.dsaonline.data.entity.HeldEntity;
import de.failender.dsaonline.data.repository.GruppeRepository;
import de.failender.dsaonline.data.repository.UserRepository;
import de.failender.dsaonline.exceptions.NotAuthenticatedException;
import de.failender.dsaonline.rest.helden.HeldWithVersion;
import de.failender.dsaonline.security.SecurityUtils;
import de.failender.dsaonline.service.HeldRepositoryService;
import de.failender.dsaonline.service.HeldenService;
import de.failender.heldensoftware.api.HeldenApi;
import de.failender.heldensoftware.api.authentication.TokenAuthentication;
import de.failender.heldensoftware.api.requests.GetAllHeldenRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/gruppen")
@Slf4j
public class GruppenController {


	@Autowired
	private GruppeRepository gruppeRepository;

	@Autowired
	private HeldRepositoryService heldRepositoryService;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private HeldenApi heldenApi;

	@Autowired
	private HeldenService heldenService;

	@GetMapping
	public List<DropdownData> getAllGruppen() {
		return this.gruppeRepository.findAll()
				.stream()
				.map(gruppe -> new DropdownData(gruppe.getName(), gruppe.getId()))
				.collect(Collectors.toList());
	}

	@PostMapping("{heldid}/{gruppeid}")
	public void editHeldenGruppe(@PathVariable BigInteger heldid, @PathVariable Integer gruppeid) {
		HeldEntity heldEntity = this.heldRepositoryService.findHeld(heldid);
		this.heldRepositoryService.updateHeldenGruppe(gruppeid, heldid);
	}

	@GetMapping("includeHelden")
	public Collection<GruppeIncludingHelden> getGruppenIncludingHelden(@RequestParam("publicOnly") boolean publicOnly, @RequestParam("showInactive")boolean showInactive) {
		if(!publicOnly) {
			SecurityUtils.checkRight(SecurityUtils.VIEW_ALL);
		}
		List<GruppeEntity> gruppen = gruppeRepository.findAll();
		Map<String, GruppeIncludingHelden> value = gruppen
				.stream()
				.collect(Collectors.toMap(gruppe -> gruppe.getName(), gruppe -> new GruppeIncludingHelden(gruppe.getName(), gruppe.getId(), new ArrayList<>())));
		Flux.fromIterable(userRepository.findAll())
				.filter(user -> user.getToken() != null)
				.flatMap(user -> heldenApi.request(new GetAllHeldenRequest(new TokenAuthentication(user.getToken()))))
				.flatMapIterable(val -> val.getHeld())
				.map(held -> {try {
					return this.heldRepositoryService.findHeldWithLatestVersion(held.getHeldenid());
					} catch(NotAuthenticatedException | org.springframework.security.access.AccessDeniedException e) {
						return new HeldWithVersion(null, null);
					}
				})
				.filter(heldWithVersion -> {
					if(heldWithVersion.getHeld() == null) {
						return false;
					}
					if(publicOnly && !heldWithVersion.getHeld().isPublic()) {
						return false;
					}
					if(!showInactive && ! heldWithVersion.getHeld().isActive()) {
						return false;
					}
					return true;
				})
				.subscribe(heldWithVersion -> value.get(heldWithVersion.getHeld().getGruppe().getName()).getHelden().add(heldenService.mapToHeldenInfo(heldWithVersion)));
		return value.values();
	}

	@GetMapping("helden/{gruppeid}")
	public List<HeldEntity> getHeldenForGruppe(@PathVariable int gruppeid) {

		return heldRepositoryService.findByGruppeId(gruppeid)
				.stream()
				.collect(Collectors.toList());
	}


}
