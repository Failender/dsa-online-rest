package de.failender.dsaonline.rest.controller;

import de.failender.dsaonline.security.AuthorizationService;
import de.failender.dsaonline.security.RestAuthentication;
import de.failender.dsaonline.service.HeldenService;
import de.failender.heldensoftware.api.HeldenApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.math.BigInteger;


@RestController
@RequestMapping("api/download")
public class DownloadController {


	@Autowired
	private AuthorizationService authorizationService;

	@Autowired
	private HeldenService heldenService;

	@GetMapping("pdf/{id}/{version}")
	public void providePdfDownload(@PathVariable BigInteger id, @PathVariable int version, HttpServletResponse response, @RequestHeader(defaultValue = "") String username, @RequestHeader(defaultValue = "") String password, RestAuthentication authentication) {
		if(!username.isEmpty()) {
			authentication.setUsername(username);
			authentication.setPassword(password);
		}
		authorizationService.authenticate(authentication);
		heldenService.providePdfDownload(id, version, response);
	}

	@GetMapping("xml/{id}/{version}")
	public void provideXmlDownload(@PathVariable BigInteger id, @PathVariable int version, HttpServletResponse response, RestAuthentication authentication, @RequestHeader(defaultValue = "") String username, @RequestHeader(defaultValue = "") String password) {
		if(!username.isEmpty()) {
			authentication.setUsername(username);
			authentication.setPassword(password);
		}
		authorizationService.authenticate(authentication);
		heldenService.provideXmlDownload(id, version, response);


	}

}
