package de.failender.dsaonline.restservice;

import de.failender.dsaonline.data.entity.VersionEntity;
import de.failender.dsaonline.service.HeldenService;
import de.failender.dsaonline.util.VersionService;
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
			assertThat(versionRepositoryService.count()).isEqualTo(versionCount);
			assertThat(versionRepositoryService.findVersionsForHeldDescending(testHeld)
					.stream()
					.map(VersionEntity::getVersion)
					.toArray(Integer[]::new))
					.isEqualTo(versions());

			assertThat(versionRepositoryService.findVersion(testHeld, 2).getLastEvent().equals("Fake1"));
			assertThat(versionRepositoryService.findVersion(testHeld, 1).getLastEvent().equals(lastEvent));
		};
		versionCount = 2;
		highestVersion = 2;
		heldenContext.setLastEreignis("Fake1");
		VersionService versionService = new VersionService(heldRepositoryService, versionRepositoryService, heldenApi,
                userRepository, heldenService);
		versionService.saveHeld(TEST_HELD_ID, heldxml().stand(heldenContext.getStand()-1000L).key(0L).asString());
		validator.run();
		//Make sure faking the same held twice wont break anythings
		versionService.saveHeld(TEST_HELD_ID, heldxml().stand(heldenContext.getStand()-1000L).key(0L).asString());
		validator.run();

		highestVersion = 3;
		versionCount = 3;
		heldenContext.setLastEreignis("Fake2");
		versionService.saveHeld(TEST_HELD_ID, heldxml().stand(heldenContext.getStand()+1000L).key(0L).asString());
		validator.run();
		assertThat(versionRepositoryService.findVersion(testHeld, 3).getLastEvent().equals("Fake2"));
	}

	private int[] versions() {
		return IntStream.range(1, highestVersion + 1)
				.map(i -> (highestVersion + 1)- i + 1- 1)
				.toArray();
	}
}
