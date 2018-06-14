package de.failender.dsaonline.restservice;

import de.failender.heldensoftware.api.HeldenApi;
import de.failender.heldensoftware.api.requests.GetAllHeldenRequest;
import org.assertj.core.api.Assertions;
import org.flywaydb.test.annotation.FlywayTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static de.failender.dsaonline.security.SecurityUtils.getAuthentication;

public class DateTest extends DsaOnlineTest {

	@Autowired
	private HeldenApi heldenApi;

	@Test
	@FlywayTest
	public void test() {
		Assertions.assertThat(heldenApi.request(new GetAllHeldenRequest(getAuthentication()), true).getHeld().size()).isEqualTo(0);

	}
}
