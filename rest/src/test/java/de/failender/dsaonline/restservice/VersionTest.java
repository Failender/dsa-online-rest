package de.failender.dsaonline.restservice;

import de.failender.dsaonline.data.entity.VersionEntity;
import de.failender.dsaonline.restservice.helper.DatenBuilder;
import de.failender.dsaonline.service.HeldenService;
import de.failender.heldensoftware.xml.datenxml.Daten;
import org.assertj.core.api.Assertions;
import org.flywaydb.test.annotation.FlywayTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import javax.xml.transform.TransformerException;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.UUID;

import static de.failender.dsaonline.restservice.helper.DatenBuilder.*;
import static de.failender.dsaonline.restservice.helper.EreignisBuilder.ereignis;
import static de.failender.dsaonline.restservice.helper.HeldXmlBuilder.heldxml;
import static de.failender.heldensoftware.api.HeldenApi.Format.heldenxml;
import static org.assertj.core.api.Assertions.assertThat;

public class VersionTest extends HeldenTest {


	@Autowired
	private HeldenService heldenService;

	@FlywayTest
	@Test
	public void testUpdateOnNewVersion() {
		String oldEvent = heldenContext.getLastEreignis();
		userHeldenService.updateHeldenForUser(userRepository.findByName(TEST_USER_NAME), false);
		//Make sure updating the held without doing anything wont create a new version
		assertThat(versionRepositoryService.count()).isEqualTo(1);
		heldenContext.setStand(heldenContext.getStand() + 1000);
		heldenContext.setLastEreignis("Test2");

		//Update and check if it updates as expected
		userHeldenService.updateHeldenForUser(userRepository.findByName(TEST_USER_NAME), false);
		assertThat(versionRepositoryService.count()).isEqualTo(2);
		List<VersionEntity> versions = versionRepositoryService.findVersionsForHeldDescending(testHeld);
		assertThat(versions
				.stream().map(VersionEntity::getVersion).toArray(Integer[]::new)).isEqualTo(new Integer[]{2,1});
		assertThat(versions.get(1).getLastEvent()).isEqualTo(oldEvent);
		assertThat(versions.get(0).getLastEvent()).isEqualTo("Test2");

		//Trigger another update and make sure it is still working
		userHeldenService.updateHeldenForUser(userRepository.findByName(TEST_USER_NAME), false);
		assertThat(versionRepositoryService.count()).isEqualTo(2);
		versions = versionRepositoryService.findVersionsForHeldDescending(testHeld);
		assertThat(versions
				.stream().map(VersionEntity::getVersion).toArray(Integer[]::new)).isEqualTo(new Integer[]{2,1});
		assertThat(versions.get(1).getLastEvent()).isEqualTo(oldEvent);
		assertThat(versions.get(0).getLastEvent()).isEqualTo("Test2");
	}

	@FlywayTest
	@Test
	public void testDeleteVersion() {
		String oldEvent = heldenContext.getLastEreignis();
		heldenContext.setStand(heldenContext.getStand() + 1000);
		heldenContext.setLastEreignis("V2");
		userHeldenService.updateHeldenForUser(userRepository.findByName(TEST_USER_NAME), false);

		heldenContext.setStand(heldenContext.getStand() + 1000);
		heldenContext.setLastEreignis("V3");
		userHeldenService.updateHeldenForUser(userRepository.findByName(TEST_USER_NAME), false);

		heldenContext.setStand(heldenContext.getStand() + 1000);
		heldenContext.setLastEreignis("V4");
		userHeldenService.updateHeldenForUser(userRepository.findByName(TEST_USER_NAME), false);


		heldenService.deleteVersion(heldenContext.getHeldid(), 3);

		List<VersionEntity> versions = versionRepositoryService.findVersionsForHeldDescending(testHeld);
		assertThat(versions
				.stream().map(VersionEntity::getVersion).toArray(Integer[]::new)).isEqualTo(new Integer[]{3,2,1});
		assertThat(versions.get(0).getLastEvent()).isEqualTo("V4");
		assertThat(versions.get(1).getLastEvent()).isEqualTo("V2");
		assertThat(versions.get(2).getLastEvent()).isEqualTo(oldEvent);
	}

	@FlywayTest
	@Test
	public void testVersionServiceValidateVersion() {
		Daten daten = daten()
				.addEreignis(ereignis(heldenContext))
				.apGesamt(1000L)
				.build();
		String xml = "";
		try {
			xml = new String(heldxml()
					.stand(1000L)
					.build());
		} catch (TransformerException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		versionService.persistVersion(testHeld, userRepository.findAll().get(0), 2, xml, UUID.randomUUID(),daten );
		Assertions.assertThat(versionService.validateVersion(testHeld)).isEqualTo(true);
		versionService.persistVersion(testHeld, userRepository.findAll().get(0), 2, xml, UUID.randomUUID(),daten );
		Assertions.assertThat(versionService.validateVersion(testHeld)).isEqualTo(false);
	}
}
