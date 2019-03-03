package de.failender.dsaonline.rest.controller;

import de.failender.dsaonline.rest.dto.AddWaehrungDto;
import de.failender.dsaonline.rest.dto.DropdownData;
import de.failender.dsaonline.service.GeldService;
import de.failender.heldensoftware.xml.datenxml.Münze;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;
import java.util.List;

@RestController
@RequestMapping("api/geld/")
public class GeldController {


	private final GeldService geldService;

	public GeldController(GeldService geldService) {
		this.geldService = geldService;
	}

	@GetMapping("{heldid}")
	public List<Münze> getMünzen(@PathVariable BigInteger heldid) {
		return geldService.getMünzen(heldid);
	}

	@GetMapping("waehrungen")
	private List<DropdownData> getWaehrungen() {
		return geldService.getWaehrungen();
	}

	@PostMapping("{heldid}/add")
	public List<Münze> add(@PathVariable BigInteger heldid, @RequestBody AddWaehrungDto dto) {
		return geldService.add(heldid, dto);
	}
}
