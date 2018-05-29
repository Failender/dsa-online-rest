package de.failender.dsaonline.rest.download;

import de.failender.dsaonline.service.CachingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.math.BigInteger;

@RestController
@RequestMapping("download")
public class DownloadController {

	@Autowired
	private CachingService cachingService;

	@GetMapping("pdf/{id}/{version}")
	public void providePdfDownload(@PathVariable BigInteger heldid, @PathVariable int version, HttpServletResponse response) {
		cachingService.provideDownload(heldid, version, response, CachingService.CacheType.pdf);
	}

	@GetMapping("xml/{id}/{version}")
	public void provideXmlDownload(@PathVariable BigInteger heldid, @PathVariable int version, HttpServletResponse response) {
		cachingService.provideDownload(heldid, version, response, CachingService.CacheType.xml);
	}

}
