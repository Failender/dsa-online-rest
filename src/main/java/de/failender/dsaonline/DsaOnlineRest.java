package de.failender.dsaonline;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class DsaOnlineRest {

	public static void main(String[] args) {
		SpringApplication.run(DsaOnlineRest.class, args);
	}
}
