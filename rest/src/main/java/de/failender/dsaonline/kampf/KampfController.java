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
	public Kampf startKampf(@PathVariable int gruppe) {
		return kampfService.startKampf(gruppe);
	}

	@GetMapping("gruppe/{gruppe}")
	public Kampf getKampfForGruppe(@PathVariable int gruppe) {
		return kampfService.getKampfForGruppe(gruppe);
	}

	@PutMapping("{kampfid}/gegner")
	public void updateGegner(@PathVariable int kampfid, @RequestBody Gegner gegner) {
		this.kampfService.updateGegner(kampfid, gegner);
	}

	@PutMapping("{kampfid}/scale")
	public void updateScale(@PathVariable int kampfid, @RequestBody float scale) {
		this.kampfService.updateScale(kampfid, scale);
	}

	@PutMapping("{kampfid}/image")
	public void updateImage(@PathVariable int kampfid, @RequestBody String image) {
		this.kampfService.updateImage(kampfid, image);
	}

}
