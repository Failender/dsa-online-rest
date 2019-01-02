package de.failender.dsaonline.data.repository;

import de.failender.dsaonline.data.entity.UserEntity;
import de.failender.dsaonline.rest.dto.UserRegistration;
import de.failender.dsaonline.restservice.DsaOnlineTest;
import de.failender.dsaonline.security.SecurityUtils;
import de.failender.dsaonline.service.MeisterService;
import de.failender.dsaonline.service.UserService;

import org.assertj.core.api.Assertions;
import org.flywaydb.test.annotation.FlywayTest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.AccessDeniedException;

public class UserRepositoryTest extends DsaOnlineTest {

	@Autowired
	private MeisterService meisterService;

	@Autowired
	private UserService userService;

	private UserEntity testUser;
	@Before
	public void createTestUser() {
		UserRegistration registration = new UserRegistration("Test", null, null, "Der Runde Tisch");

		testUser = userService.registerUser(registration);
	}

	@FlywayTest
	public void addMeisterForGruppeShouldAddMeisterPermission() {
		Assertions.assertThat(userRepository.findRoleNamesForUser(testUser.getId()).contains(SecurityUtils.MEISTER)).isEqualTo(false);
		meisterService.addMeisterForGruppe(testUser.getGruppe().getId(), testUser.getId());
		Assertions.assertThat(userRepository.findRoleNamesForUser(testUser.getId()).contains(SecurityUtils.MEISTER)).isEqualTo(false);
	}

	@FlywayTest
	@Test
	public void testAddMeister() {
		userService.addUserRole(testUser, "Meister");
		Assertions.assertThat(meisterService.getMeisterForGruppe(testUser.getGruppe().getId()).size()).isEqualTo(0);
		meisterService.addMeisterForGruppe(testUser.getGruppe().getId(), testUser.getId());
		Assertions.assertThat(meisterService.getMeisterForGruppe(testUser.getGruppe().getId()).size()).isEqualTo(1);
	}

	@FlywayTest
	@Test(expected = DataIntegrityViolationException.class)

	public void testExceptionOnMultiAddMeister() {
		userService.addUserRole(testUser, "Meister");

		meisterService.addMeisterForGruppe(testUser.getGruppe().getId(), testUser.getId());
		meisterService.addMeisterForGruppe(testUser.getGruppe().getId(), testUser.getId());

	}

	@FlywayTest
	@Test
	public void testRemoveMeister() {
		testAddMeister();
		meisterService.removeMeisterForGruppe(testUser.getGruppe().getId(), testUser.getId());
		Assertions.assertThat(meisterService.getMeisterForGruppe(testUser.getGruppe().getId()).size()).isEqualTo(0);
	}


}
