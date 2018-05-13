package de.failender.dsaonline.restservice;

import de.failender.dsaonline.service.ApiService;
import org.assertj.core.api.Assertions;
import org.flywaydb.test.annotation.FlywayTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class DateTest extends DsaOnlineTest {

	@Autowired
	private ApiService apiService;

	@Test
	@FlywayTest
	public void test() {
		Assertions.assertThat(apiService.getAllHelden().size()).isEqualTo(3);

	}
}
