package de.failender.dsaonline.service;

import de.failender.dsaonline.restservice.DsaOnlineTest;
import org.assertj.core.api.Assertions;
import org.flywaydb.test.annotation.FlywayTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class MeisterServiceTest  extends DsaOnlineTest {

	private static final int TEST_GRUPPE_ID = 1;

	@Autowired
	private MeisterService meisterService;

	@FlywayTest
	@Test
	public void testMeisterAdd() {
		Assertions.assertThat(meisterService.getMeisterForGruppe(TEST_GRUPPE_ID).size()).isEqualTo(0);
		meisterService.addMeisterForGruppe(TEST_GRUPPE_ID, 1);
		Assertions.assertThat(meisterService.getMeisterForGruppe(TEST_GRUPPE_ID).size()).isEqualTo(1);
		meisterService.removeMeisterForGruppe(TEST_GRUPPE_ID, 1);
		Assertions.assertThat(meisterService.getMeisterForGruppe(TEST_GRUPPE_ID).size()).isEqualTo(0);
	}
}
