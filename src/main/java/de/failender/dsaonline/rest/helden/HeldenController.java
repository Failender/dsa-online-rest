package de.failender.dsaonline.rest.helden;

import de.failender.dsaonline.security.SecurityUtils;
import de.failender.dsaonline.service.HeldenService;
import de.failender.dsaonline.service.UserHeldenService;
import de.failender.dsaonline.util.VersionFakeService;
import de.failender.heldensoftware.xml.datenxml.Daten;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigInteger;
import java.util.List;

@RestController
@RequestMapping("api/helden")
@Slf4j
public class HeldenController {

	@Autowired
	private HeldenService heldenService;
	@Autowired
	private UserHeldenService userHeldenService;

	@Autowired
	private VersionFakeService versionFakeService;

	@GetMapping
	public List<HeldenInfo> getAllHeldenForCurrentUser() {
		return heldenService.getAllHeldenForCurrentUser();
	}

	@GetMapping("held/{id}/{version}")
	public Daten getHeldenDaten(@PathVariable BigInteger id, @PathVariable int version) {
		return heldenService.getHeldenDaten(id, version);
	}

	@GetMapping("held/unterschied/{heldenid}/{from}/{to}")
	public HeldenUnterschied calculateUnterschied(@PathVariable BigInteger heldenid, @PathVariable int from, @PathVariable int to) {
		return this.heldenService.calculateUnterschied(heldenid, from, to);
	}

	@GetMapping("held/versionen/{heldenid}")
	public List<HeldVersion> loadHeldenVersionen(@PathVariable BigInteger heldenid) {
		return heldenService.loadHeldenVersionen(heldenid);
	}

	@GetMapping("held/pdf/{id}/{version}")
	public void providePdfDownload(@PathVariable BigInteger id, @PathVariable int version, HttpServletResponse response) throws FileNotFoundException {
		heldenService.providePdfDownload(id, version, response);
	}

	@GetMapping("reload")
	public List<HeldenInfo> reloadHelden() {
		userHeldenService.forceUpdateHeldenForUser(SecurityUtils.getCurrentUser());
		return heldenService.getAllHeldenForCurrentUser();
	}

	@PostMapping("{heldid}/upload")
	public void uploadVersion(@RequestParam("file") MultipartFile[] file, @PathVariable BigInteger heldid) throws IOException {
		for (MultipartFile multipartFile : file) {
			String xml = IOUtils.toString(multipartFile.getInputStream(), "UTF-8");
			versionFakeService.fakeHeld(heldid, xml);
		}

	}

	@PostMapping("public/{heldid}/{isPublic}")
	public void editPublic(@PathVariable BigInteger heldid, @PathVariable boolean isPublic) {
		heldenService.updateHeldenPublic(isPublic, heldid);
	}


}
