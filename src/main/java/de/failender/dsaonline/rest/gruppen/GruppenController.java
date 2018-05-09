package de.failender.dsaonline.rest.gruppen;

import de.failender.dsaonline.data.repository.GruppeRepository;
import de.failender.dsaonline.data.repository.HeldRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/gruppen")
public class GruppenController {


	@Autowired
	private GruppeRepository gruppeRepository;

	@Autowired
	private HeldRepository heldRepository;

	@GetMapping
	public List<DropdownData> getAllGruppen() {
		return this.gruppeRepository.findAll()
				.stream()
				.map(gruppe -> new DropdownData(gruppe.getName(), gruppe.getId()))
				.collect(Collectors.toList());
	}

	@PostMapping("{heldid}/{gruppeid}")
	public void editHeldenGruppe(@PathVariable BigInteger heldid, @PathVariable Integer gruppeid) {
		this.heldRepository.updateHeldenGruppe(gruppeid,heldid);
	}
}
