package de.failender.dsaonline.restservice;

import de.failender.dsaonline.data.entity.UserEntity;
import org.flywaydb.test.annotation.FlywayTest;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class UserServiceTest extends HeldenTest {

	@FlywayTest
	@Test
	public void testRegistration() {
		assertThat(userRepository.count()).isEqualTo(2);
		assertThat(userRepository.findAll()
				.stream()
				.map(UserEntity::getName)
				.toArray(String[]::new))
				.isEqualTo(new String[]{"Admin", TEST_USER_NAME});
		assertThat(heldRepository.count()).isEqualTo(1);
		assertThat(heldRepository.findAll().get(0).getName()).isEqualTo(TEST_HELD_NAME);
		assertThat(heldRepository.findAll().get(0).getId()).isEqualTo(TEST_HELD_ID);
		assertThat(versionRepositoryService.count()).isEqualTo(1);
		assertThat(versionRepositoryService.findAll().iterator().next().getVersion()).isEqualTo(1);
		assertThat(versionRepositoryService.findAll().iterator().next().getLastEvent()).isEqualTo(heldenContext.getLastEreignis());

	}
}
