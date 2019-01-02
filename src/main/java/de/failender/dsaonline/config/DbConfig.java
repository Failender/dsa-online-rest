// Generated by delombok at Thu Nov 22 18:54:10 CET 2018
package de.failender.dsaonline.config;

import de.failender.dsaonline.migrations.SetupConfiguration;
import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class DbConfig {

	private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(DbConfig.class);

	@Bean
	public Flyway flyway(DataSource theDataSource, @Value("${dsa.online.clean.on.start}") boolean cleanOnStart, SetupConfiguration setupConfiguration) {
		Flyway flyway = Flyway.configure().locations("classpath:db/migration", "classpath:de.failender.dsaonline.migrations")
				.dataSource(theDataSource).load();
		if (cleanOnStart) {
			flyway.clean();
		}
		flyway.migrate();
		return flyway;
	}
}
