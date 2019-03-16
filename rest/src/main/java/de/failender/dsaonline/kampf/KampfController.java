package de.failender.dsaonline.kampf;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/kampf")
public class KampfController {


	private final KampfService kampfService;

	public KampfController(KampfService kampfService) {
		this.kampfService = kampfService;

	}

	@PostMapping("start/{gruppe}")
	public Kampf startKampf(@PathVariable int gruppe, @RequestBody Kampf kampf) {
		return kampfService.startKampf(gruppe, kampf);
	}

	@GetMapping("gruppe/{gruppe}")
	public Kampf getKampfForGruppe(@PathVariable int gruppe) {
		return kampfService.getKampfForGruppe(gruppe);
	}

	@PutMapping("{kampfid}/gegner")
	public void updateGegner(@PathVariable int kampfid, @RequestBody Gegner gegner) {
		this.kampfService.updateGegner(kampfid, gegner);
	}

	@PutMapping("{kampfid}/image")
	public void updateImage(@PathVariable int kampfid, @RequestBody String image) {
		this.kampfService.updateImage(kampfid, image);
	}

	@PostMapping("{kampfid}/component")
	public void addComponent(@PathVariable int kampfid, @RequestBody KampfComponent component) {
		this.kampfService.addComponent(kampfid, component);
	}

	@PutMapping("{kampfid}/component")
	public void updateComponent(@PathVariable int kampfid, @RequestBody KampfComponent component) {
		this.kampfService.updateComponent(kampfid, component);
	}




}
