package de.failender.dsaonline.rest.controller;

import de.failender.dsaonline.data.entity.KampagneEntity;
import de.failender.dsaonline.service.KampagnenService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/kampagnen")
public class KampagneController {

	private final KampagnenService kampagnenService;

	public KampagneController(KampagnenService kampagnenService) {
		this.kampagnenService = kampagnenService;
	}

	@GetMapping("{gruppeid}")
	public List<KampagneEntity> findKampagnenByGruppeId(@PathVariable int gruppeid) {
		return kampagnenService.findKampagneByGruppe(gruppeid);
	}

	@PostMapping("create/{name}/{gruppeid}")
	public void createKampagneEntity(@PathVariable String name, @PathVariable int gruppeid) {
		kampagnenService.createKampagne(name, gruppeid);
	}

	@DeleteMapping("kampagne/{kampagneid}")
	public void deleteKampagne(@PathVariable int kampagneid) {
		kampagnenService.deleteKampagne(kampagneid);
	}

	@GetMapping("kampagne/{kampagneid}")
	public KampagneEntity getKampagneById(@PathVariable int kampagneid) {
		return kampagnenService.getKampagneById(kampagneid);
	}
}
