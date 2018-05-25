package de.failender.dsaonline.restservice;

import de.failender.dsaonline.data.entity.UserEntity;
import de.failender.dsaonline.data.repository.GruppeRepository;
import de.failender.dsaonline.data.repository.HeldRepository;
import de.failender.dsaonline.data.repository.UserRepository;
import de.failender.dsaonline.rest.user.UserRegistration;
import de.failender.dsaonline.service.ApiService;
import de.failender.dsaonline.service.CachingService;
import de.failender.dsaonline.service.UserHeldenService;
import de.failender.dsaonline.service.UserService;
import de.failender.dsaonline.util.JaxbUtil;
import de.failender.dsaonline.util.VersionFakeService;
import org.assertj.core.api.Assertions;
import org.flywaydb.test.annotation.FlywayTest;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigInteger;

public class VersionTest extends DsaOnlineTest {


	@Autowired
	private HeldRepository heldRepository;

	@Autowired
	private UserService userService;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private GruppeRepository gruppeRepository;

	@Autowired
	private VersionFakeService versionFakeService;

	@Autowired
	private CachingService cachingService;


	private static final String FAKE_TOKEN ="token";
	private static final String TEST_GRUPPE = "Der Runde Tisch";
	private static final BigInteger TORI_ID = BigInteger.valueOf(36222L);
	@FlywayTest
	@Test
	public void testVersioning() {
		ApiService apiService = Mockito.mock(ApiService.class);
		Mockito.when(apiService.getAllHelden(FAKE_TOKEN)).thenReturn(
				JaxbUtil.heldenFromStream(getResource("helden/all.xml")),
				JaxbUtil.heldenFromStream(getResource("helden/all2.xml")));
		UserHeldenService userHeldenService = new UserHeldenService(heldRepository, userRepository, apiService, versionFakeService, cachingService);
		UserService userService = new UserService(userRepository, gruppeRepository, userHeldenService);
		UserEntity userEntity = userService.registerUser(new UserRegistration("TEST", null, FAKE_TOKEN, TEST_GRUPPE));

		Assertions.assertThat(heldRepository.findAll().size()).isEqualTo(1);
		userHeldenService.updateHeldenForUser(userEntity);
		heldRepository.findAll().forEach(System.out::println);
		Assertions.assertThat(heldRepository.findAll().size()).isEqualTo(2);

		Assertions.assertThat(heldRepository.findFirstByIdIdOrderByIdVersionDesc(TORI_ID).get().getVersion()).isEqualTo(2);
	}
}
