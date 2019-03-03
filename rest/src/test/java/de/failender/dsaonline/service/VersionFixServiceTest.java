package de.failender.dsaonline.service;

import com.fasterxml.jackson.databind.node.BigIntegerNode;
import de.failender.dsaonline.data.entity.VersionEntity;
import de.failender.dsaonline.data.repository.VersionRepository;
import de.failender.dsaonline.data.service.VersionFixService;
import de.failender.dsaonline.restservice.DsaOnlineTest;
import net.bytebuddy.asm.Advice;
import org.assertj.core.api.Assertions;
import org.flywaydb.test.annotation.FlywayTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigInteger;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class VersionFixServiceTest extends DsaOnlineTest {

	@Autowired
	private VersionRepository versionRepository;

	@Autowired
	private VersionFixService versionFixService;

	@Test
	@FlywayTest
	public void testVersionFix() {
		BigInteger heldid = BigInteger.ONE;
		versionRepository.save(versionEntity(heldid, 1, new Date(2000L), 200, "EVT"));
		versionRepository.save(versionEntity(heldid, 2, new Date(3000L), 200, "EVT"));
		versionRepository.save(versionEntity(heldid, 2, new Date(4000L), 200, "EVT"));
		versionRepository.save(versionEntity(heldid, 3, new Date(5000L), 200, "EVT"));
		versionRepository.save(versionEntity(heldid, 4, new Date(6000L), 200, "EVT"));
		versionRepository.save(versionEntity(heldid, 4, new Date(7000L), 200, "EVT"));
		versionRepository.save(versionEntity(heldid, 5, new Date(8000L), 200, "EVT"));
		versionRepository.save(versionEntity(heldid, 5, new Date(8000L), 200, "EVT"));
		versionRepository.save(versionEntity(heldid, 6, new Date(1000L), 200, "EVT"));
		versionFixService.fixVersions();

		long pre = Long.MAX_VALUE;
		List<VersionEntity> versionEntityList = versionRepository.findByHeldidOrderByVersionDesc(heldid);
		for(VersionEntity versionEntity: versionRepository.findByHeldidOrderByVersionDesc(heldid)) {
			if(versionEntity.getCreatedDate().getTime() >= pre) {

				System.err.println(versionEntity.getCreatedDate().getTime() + "   " + pre);
				Assertions.fail("Ordering failed");
			}
			pre = versionEntity.getCreatedDate().getTime();
		}


	}

	private VersionEntity versionEntity(BigInteger heldid, int version, Date created, int ap, String lastEvent) {
		VersionEntity versionEntity = new VersionEntity();
		versionEntity.setHeldid(heldid);
		versionEntity.setVersion(version);
		versionEntity.setAp(ap);
		versionEntity.setCreatedDate(created);
		versionEntity.setLastEvent(lastEvent);
		versionEntity.setCacheId(UUID.randomUUID());
		return versionEntity;
	}
}
