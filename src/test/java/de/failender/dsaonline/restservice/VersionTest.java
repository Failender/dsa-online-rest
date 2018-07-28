package de.failender.dsaonline.restservice;

import de.failender.dsaonline.data.entity.VersionEntity;
import org.flywaydb.test.annotation.FlywayTest;
import org.junit.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class VersionTest extends HeldenTest {




	@FlywayTest
	@Test
	public void testUpdateOnNewVersion() {
		String oldEvent = heldenContext.getLastEreignis();
		userHeldenService.updateHeldenForUser(userRepository.findByName(TEST_USER_NAME), false);
		//Make sure updating the held without doing anything wont create a new version
		assertThat(versionRepository.count()).isEqualTo(1);
		heldenContext.setStand(heldenContext.getStand() + 1000);
		heldenContext.setLastEreignis("Test2");

		//Update and check if it updates as expected
		userHeldenService.updateHeldenForUser(userRepository.findByName(TEST_USER_NAME), false);
		assertThat(versionRepository.count()).isEqualTo(2);
		List<VersionEntity> versions = versionRepository.findByHeldidOrderByVersionDesc(heldenContext.getHeldid());
		assertThat(versions
				.stream().map(VersionEntity::getVersion).toArray(Integer[]::new)).isEqualTo(new Integer[]{2,1});
		assertThat(versions.get(1).getLastEvent()).isEqualTo(oldEvent);
		assertThat(versions.get(0).getLastEvent()).isEqualTo("Test2");

		//Trigger another update and make sure it is still working
		userHeldenService.updateHeldenForUser(userRepository.findByName(TEST_USER_NAME), false);
		assertThat(versionRepository.count()).isEqualTo(2);
		versions = versionRepository.findByHeldidOrderByVersionDesc(heldenContext.getHeldid());
		assertThat(versions
				.stream().map(VersionEntity::getVersion).toArray(Integer[]::new)).isEqualTo(new Integer[]{2,1});
		assertThat(versions.get(1).getLastEvent()).isEqualTo(oldEvent);
		assertThat(versions.get(0).getLastEvent()).isEqualTo("Test2");
	}
}
