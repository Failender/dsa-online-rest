package de.failender.dsaonline.rest.helden;

import de.failender.dsaonline.security.SecurityUtils;
import de.failender.dsaonline.service.HeldenService;
import de.failender.dsaonline.service.UserHeldenService;
import de.failender.heldensoftware.xml.datenxml.Daten;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.FileNotFoundException;
import java.math.BigInteger;
import java.util.List;

@RestController
@RequestMapping("api/helden")
public class HeldenController {

	@Autowired
	private HeldenService heldenService;
	@Autowired
	private UserHeldenService userHeldenService;

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

	@GetMapping("held/pdf/{id}/{version}")
	public ResponseEntity<InputStreamResource> providePdfDownload(@PathVariable BigInteger id, @PathVariable int version) throws FileNotFoundException {
		return heldenService.providePdfDownload(id, version);
	}

	@GetMapping("reload")
	public List<HeldenInfo> reloadHelden() {
		userHeldenService.forceUpdateHeldenForUser(SecurityUtils.getCurrentUser());
		return heldenService.getAllHeldenForCurrentUser();
	}


}
