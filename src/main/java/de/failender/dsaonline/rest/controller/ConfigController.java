package de.failender.dsaonline.rest.controller;

import de.failender.dsaonline.rest.dto.VersionsDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/config")
public class ConfigController {

	@Value("${dsa.online.backend.version}")
	private String backendVersion;

	@Value("${dsa.online.frontend.version}")
	private String frontendVersion;

	@GetMapping("version")
	public VersionsDTO getVersions() {
		return new VersionsDTO(frontendVersion, backendVersion);
	}
}
