package de.failender.dsaonline.util;

import de.failender.dsaonline.data.entity.HeldEntity;
import de.failender.dsaonline.data.entity.UserEntity;
import de.failender.dsaonline.data.entity.VersionEntity;
import de.failender.dsaonline.data.repository.UserRepository;
import de.failender.dsaonline.rest.helden.HeldVersion;
import de.failender.dsaonline.service.HeldRepositoryService;
import de.failender.dsaonline.service.HeldenService;
import de.failender.dsaonline.service.UserHeldenService;
import de.failender.heldensoftware.api.HeldenApi;
import de.failender.heldensoftware.api.Helper;
import de.failender.heldensoftware.api.requests.ConvertingRequest;
import de.failender.heldensoftware.api.requests.ReturnHeldDatenWithEreignisseRequest;
import de.failender.heldensoftware.api.requests.ReturnHeldPdfRequest;
import de.failender.heldensoftware.api.requests.ReturnHeldXmlRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.math.BigInteger;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class VersionFakeService {

	private final HeldRepositoryService heldRepositoryService;

	private final HeldenApi heldenApi;

	private final UserHeldenService userHeldenService;

	private final UserRepository userRepository;

	private final HeldenService heldenService;

	private boolean testing = false;

	public VersionFakeService(HeldRepositoryService heldRepositoryService, HeldenApi heldenApi, UserHeldenService userHeldenService, UserRepository userRepository, HeldenService heldenService) {
		this.heldRepositoryService = heldRepositoryService;
		this.heldenApi = heldenApi;
		this.userHeldenService = userHeldenService;
		this.userRepository = userRepository;
		this.heldenService = heldenService;
	}

	private void fakeDatenXml(InputStream is, BigInteger heldid, int version) {
		ReturnHeldDatenWithEreignisseRequest req = new ReturnHeldDatenWithEreignisseRequest(heldid, null, version);
		heldenApi.getCacheHandler().doCache(req, is);

	}

	private void fakeHeldenXml(InputStream is, BigInteger heldid, int version) {
		ReturnHeldXmlRequest req = new ReturnHeldXmlRequest(heldid, null, version);
		heldenApi.getCacheHandler().doCache(req, is);

	}

	private void fakePdf(InputStream is, BigInteger heldid, int version) {
		ReturnHeldPdfRequest req = new ReturnHeldPdfRequest(heldid, null, version);
		heldenApi.getCacheHandler().doCache(req, is);

	}

	public void fakeHeld(BigInteger id, String xml) {
		Tuple2<InputStream, InputStream> converted = Mono.zip(heldenApi.requestRaw(new ConvertingRequest(HeldenApi.Format.datenxml, xml), true),
				heldenApi.requestRaw(new ConvertingRequest(HeldenApi.Format.pdfintern, xml), true))
				.block();
		HeldEntity held = heldRepositoryService.findHeld(id);
		UserEntity userEntity = userRepository.findById(held.getUserId()).get();
		Date stand = XmlUtil.getStandFromString(xml);
		List<HeldVersion> heldVersions = heldenService.loadHeldenVersionen(id);
		HeldVersion latestVersion = heldVersions.get(0);
		HeldVersion firstVersionToMove = heldVersions
				.stream()
				.filter(version -> version.getDatum().getTime() >= stand.getTime())
				.sorted(Comparator.comparingLong(a -> a.getDatum().getTime()))
				.findFirst()
				.orElse(null);
		int firstVersionToMoveInt = firstVersionToMove == null? 1: firstVersionToMove.getVersion();
		if(firstVersionToMove.getDatum().getTime() == stand.getTime()){
			log.warn("Skipping version fake for held {} since it already exists", id);
			return;
		}
		for(int i=latestVersion.getVersion(); i>=firstVersionToMoveInt; i--) {
			VersionEntity versionEntity = heldRepositoryService.findVersion(id, i);
			versionEntity.setVersion(i + 1);
			heldRepositoryService.saveVersion(versionEntity);
			if(!testing) {
				Helper.copyFilesToHigherVersion(id, i, heldenApi.getCacheHandler());
			}

		}


		fakeHeldenXml(new ByteArrayInputStream(xml.getBytes()), id, firstVersionToMoveInt);
		fakeDatenXml(converted.getT1(), id, firstVersionToMoveInt);
		fakePdf(converted.getT2(), id, firstVersionToMoveInt);
		userHeldenService.persistVersion(id, userEntity, firstVersionToMoveInt, xml);

	}

	public void setTesting(boolean testing) {
		this.testing = testing;
	}
}
