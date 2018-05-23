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

	@GetMapping("held/{id}/{version}")
	public Daten getHeldenDaten(@PathVariable BigInteger id, @PathVariable int version) {
		return heldenService.getHeldenDaten(id, version);
	}

	@GetMapping("held/unterschied/{heldenid}/{from}/{to}")
	public HeldenUnterschied calculateUnterschied(@PathVariable BigInteger heldenid, @PathVariable int from,@PathVariable int to) {
		return this.heldenService.calculateUnterschied(heldenid, from, to);
	}

	@GetMapping("held/versionen/{heldenid}")
	public List<HeldVersion> loadHeldenVersionen(@PathVariable BigInteger heldenid) {
		return heldenService.loadHeldenVersionen(heldenid);
	}


}
