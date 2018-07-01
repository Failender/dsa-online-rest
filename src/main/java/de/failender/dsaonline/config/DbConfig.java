package de.failender.dsaonline.config;

import lombok.extern.slf4j.Slf4j;
import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;


@Slf4j
@Configuration
public class DbConfig {



	@ConditionalOnProperty("${dsa.online.clean.on.start}")
	@Bean
	public Flyway flyway(DataSource theDataSource) {
		Flyway flyway = new Flyway();
		flyway.setDataSource(theDataSource);
		flyway.setLocations("classpath:db/migration");
		flyway.clean();
		flyway.migrate();

		return flyway;
	}
}
