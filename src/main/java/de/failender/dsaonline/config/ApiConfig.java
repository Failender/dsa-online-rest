package de.failender.dsaonline.config;

import de.failender.heldensoftware.api.HeldenApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.io.File;

@Configuration
public class ApiConfig {

	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}

	@Bean
	public HeldenApi heldenApi(@Value("${dsa.online.cache.directory}") String cacheDirectoryString, RestTemplate restTemplate) {
		return new HeldenApi(new File(cacheDirectoryString), restTemplate);
	}
}
