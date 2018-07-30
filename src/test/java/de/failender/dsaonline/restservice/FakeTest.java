package de.failender.dsaonline.restservice;

import de.failender.dsaonline.data.entity.VersionEntity;
import de.failender.dsaonline.service.HeldenService;
import de.failender.dsaonline.util.VersionFakeService;
import org.flywaydb.test.annotation.FlywayTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.stream.IntStream;

import static de.failender.dsaonline.restservice.helper.HeldXmlBuilder.heldxml;
import static org.assertj.core.api.Assertions.assertThat;

public class FakeTest extends HeldenTest{

	@Autowired
	private HeldenService heldenService;

	private int versionCount;
	private int highestVersion;

	@FlywayTest
	@Test
	public void testFakes() throws Exception{
		String lastEvent = heldenContext.getLastEreignis();
		Runnable validator = () -> {
			assertThat(versionRepository.count()).isEqualTo(versionCount);
			assertThat(versionRepository.findByHeldidOrderByVersionDesc(heldenContext.getHeldid())
					.stream()
					.map(VersionEntity::getVersion)
					.toArray(Integer[]::new))
					.isEqualTo(versions());
			assertThat(versionRepository.findByVersionAndHeldid(2, heldenContext.getHeldid()).get().getLastEvent().equals("Fake1"));
			assertThat(versionRepository.findByVersionAndHeldid(1, heldenContext.getHeldid()).get().getLastEvent().equals(lastEvent));
		};
		versionCount = 2;
		highestVersion = 2;
		heldenContext.setLastEreignis("Fake1");
		VersionFakeService versionFakeService = new VersionFakeService(heldRepositoryService, heldenApi,
				userHeldenService, userRepository, heldenService);
		versionFakeService.setTesting(true);
		versionFakeService.fakeHeld(TEST_HELD_ID, heldxml().stand(heldenContext.getStand()-1000L).key(0L).asString());

		validator.run();
		//Make sure faking the same held twice wont break anythings
		versionFakeService.fakeHeld(TEST_HELD_ID, heldxml().stand(heldenContext.getStand()-1000L).key(0L).asString());
		validator.run();

		highestVersion = 3;
		versionCount = 3;
		heldenContext.setLastEreignis("Fake2");
		versionFakeService.fakeHeld(TEST_HELD_ID, heldxml().stand(heldenContext.getStand()+1000L).key(0L).asString());
		validator.run();
		assertThat(versionRepository.findByVersionAndHeldid(3, heldenContext.getHeldid()).get().getLastEvent().equals("Fake2"));
	}

	private int[] versions() {
		return IntStream.range(1, highestVersion + 1)
				.map(i -> (highestVersion + 1)- i + 1- 1)
				.toArray();
	}
}
