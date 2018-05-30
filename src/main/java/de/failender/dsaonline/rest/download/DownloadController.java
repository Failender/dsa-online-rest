package de.failender.dsaonline.rest.download;

import de.failender.dsaonline.security.AuthorizationService;
import de.failender.dsaonline.security.RestAuthentication;
import de.failender.dsaonline.service.CachingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.math.BigInteger;

@RestController
@RequestMapping("api/download")
public class DownloadController {

	@Autowired
	private CachingService cachingService;

	@Autowired
	private AuthorizationService authorizationService;

	@GetMapping("pdf/{id}/{version}")
	public void providePdfDownload(@PathVariable BigInteger id, @PathVariable int version, HttpServletResponse response, RestAuthentication authentication) {
		authorizationService.authenticate(authentication);
		cachingService.provideDownload(id, version, response, CachingService.CacheType.pdf);
	}

	@GetMapping("xml/{id}/{version}")
	public void provideXmlDownload(@PathVariable BigInteger id, @PathVariable int version, HttpServletResponse response, RestAuthentication authentication) {
		authorizationService.authenticate(authentication);
		cachingService.provideDownload(id, version, response, CachingService.CacheType.xml);
	}

}
