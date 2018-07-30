package de.failender.dsaonline.restservice;

import de.failender.dsaonline.data.entity.VersionEntity;
import de.failender.dsaonline.service.HeldenService;
import de.failender.dsaonline.util.VersionFakeService;
import org.flywaydb.test.annotation.FlywayTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static de.failender.dsaonline.restservice.helper.HeldXmlBuilder.heldxml;
import static org.assertj.core.api.Assertions.assertThat;

public class FakeTest extends HeldenTest{

	@Autowired
	private HeldenService heldenService;

	@FlywayTest
	@Test
	public void testFakes() throws Exception{
		String lastEvent = heldenContext.getLastEreignis();
		Runnable validator = () -> {
			assertThat(versionRepository.count()).isEqualTo(2);
			assertThat(versionRepository.findByHeldidOrderByVersionDesc(heldenContext.getHeldid())
					.stream()
					.map(VersionEntity::getVersion)
					.toArray(Integer[]::new))
					.isEqualTo(new int[]{2,1});
			assertThat(versionRepository.findByVersionAndHeldid(2, heldenContext.getHeldid()).get().getLastEvent().equals("Fake1"));
			assertThat(versionRepository.findByVersionAndHeldid(1, heldenContext.getHeldid()).get().getLastEvent().equals(lastEvent));
		};
		heldenContext.setLastEreignis("Fake1");
		VersionFakeService versionFakeService = new VersionFakeService(heldRepositoryService, heldenApi,
				userHeldenService, userRepository, heldenService);
		versionFakeService.setTesting(true);
		versionFakeService.fakeHeld(TEST_HELD_ID, heldxml().stand(heldenContext.getStand()-1000L).key(0L).asString());

		validator.run();

		versionFakeService.fakeHeld(TEST_HELD_ID, heldxml().stand(heldenContext.getStand()-1000L).key(0L).asString());
		validator.run();
	}
}
