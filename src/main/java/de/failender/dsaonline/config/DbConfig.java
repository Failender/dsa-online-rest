package de.failender.dsaonline.config;

import lombok.extern.slf4j.Slf4j;
import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;


@Slf4j
@Configuration
public class DbConfig {



	@Bean
	public Flyway flyway(DataSource theDataSource, @Value("${dsa.online.clean.on.start}")boolean cleanOnStart) {

		Flyway flyway = new Flyway();
		flyway.setDataSource(theDataSource);
		flyway.setLocations("classpath:db/migration");
		if(cleanOnStart) {
			flyway.clean();
			log.error("CLEAN");
		}

		flyway.migrate();

		return flyway;
	}
}
