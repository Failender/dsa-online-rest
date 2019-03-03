package de.failender.dsaonline.service;

import de.failender.dsaonline.data.entity.KampagneEntity;
import de.failender.dsaonline.restservice.DsaOnlineTest;
import org.assertj.core.api.Assertions;
import org.flywaydb.test.annotation.FlywayTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;


public class KampagnenServiceTest extends DsaOnlineTest {

	@Autowired
	private KampagnenService kampagnenService;

	@Test
	@FlywayTest
	public void testKampagneService() {

		int testGruppe = 1;

		kampagnenService.createKampagne("TEST", testGruppe);
		List<KampagneEntity> kampagne = kampagnenService.findKampagneByGruppe(testGruppe);
		Assertions.assertThat(kampagne.size()).isEqualTo(1);
		Assertions.assertThat(kampagne.get(0).getName()).isEqualTo("TEST");


	}
}
