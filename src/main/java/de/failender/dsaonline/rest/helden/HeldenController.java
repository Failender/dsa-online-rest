package de.failender.dsaonline.rest.helden;

import de.failender.dsaonline.service.HeldenService;
import de.failender.heldensoftware.xml.datenxml.Daten;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigInteger;
import java.util.List;

@RestController
@RequestMapping("api/helden")
public class HeldenController {

	@Autowired
	private HeldenService heldenService;

	@GetMapping
	public List<HeldenInfo> getAllHeldenForCurrentUser() {
		return heldenService.getAllHeldenForCurrentUser();
	}

	@GetMapping("held/{id}")
	public Daten getHeldenDaten(@PathVariable BigInteger id) {
		return heldenService.getHeldenDaten(id);
	}
}
