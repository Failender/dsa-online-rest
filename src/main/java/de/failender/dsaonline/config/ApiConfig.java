package de.failender.dsaonline.config;

import de.failender.heldensoftware.api.HeldenApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;

@Configuration
public class ApiConfig {

	@Bean
	public HeldenApi heldenApi(@Value("${dsa.online.cache.directory}") String cacheDirectoryString) {
		return new HeldenApi(new File(cacheDirectoryString));
	}
}
