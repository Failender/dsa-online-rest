package de.failender.dsaonline.rest.gruppen;

import de.failender.dsaonline.data.entity.GruppeEntity;
import de.failender.dsaonline.data.entity.HeldEntity;
import de.failender.dsaonline.data.repository.GruppeRepository;
import de.failender.dsaonline.data.repository.UserRepository;
import de.failender.dsaonline.exceptions.HeldNotFoundException;
import de.failender.dsaonline.rest.helden.HeldWithVersion;
import de.failender.dsaonline.security.SecurityUtils;
import de.failender.dsaonline.service.ApiService;
import de.failender.dsaonline.service.HeldRepositoryService;
import de.failender.dsaonline.service.HeldenService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
	private ApiService apiService;

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
		SecurityUtils.canCurrentUserViewHeld(heldEntity);
		this.heldRepositoryService.updateHeldenGruppe(gruppeid, heldid);
	}

	@GetMapping("includeHelden")
	public Collection<GruppeIncludingHelden> getGruppenIncludingHelden() {
		SecurityUtils.checkRight(SecurityUtils.VIEW_ALL);
		List<GruppeEntity> gruppen = gruppeRepository.findAll();
		Map<String, GruppeIncludingHelden> value = gruppen
				.stream()
				.collect(Collectors.toMap(gruppe -> gruppe.getName(), gruppe -> new GruppeIncludingHelden(gruppe.getName(), gruppe.getId(), new ArrayList<>())));
		userRepository.findAll()
				.parallelStream()
				.filter(user -> user.getToken() != null)
				.map(user -> apiService.getAllHelden(user.getToken()))
				.flatMap(List::stream)
				.forEach(held -> {
					try {
						HeldWithVersion heldWithVersion = this.heldRepositoryService.findHeldWithLatestVersion(held.getHeldenid());
						value.get(heldWithVersion.getHeld().getGruppe().getName()).getHelden().add(heldenService.mapToHeldenInfo(heldWithVersion));
					} catch (HeldNotFoundException e) {
						;
					}
				});


		return value.values();
	}
}
