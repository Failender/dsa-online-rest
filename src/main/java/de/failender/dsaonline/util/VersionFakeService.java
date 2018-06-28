package de.failender.dsaonline.util;

import de.failender.dsaonline.data.entity.VersionEntity;
import de.failender.dsaonline.exceptions.CorruptXmlException;
import de.failender.dsaonline.rest.helden.HeldenUnterschied;
import de.failender.dsaonline.service.HeldRepositoryService;
import de.failender.dsaonline.service.HeldenService;
import de.failender.heldensoftware.api.HeldenApi;
import de.failender.heldensoftware.api.Helper;
import de.failender.heldensoftware.api.requests.ReturnHeldDatenWithEreignisseRequest;
import de.failender.heldensoftware.api.requests.ReturnHeldPdfRequest;
import de.failender.heldensoftware.api.requests.ReturnHeldXmlRequest;
import de.failender.heldensoftware.xml.datenxml.Daten;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.xml.bind.JAXBException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipFile;

@Service
@Slf4j
public class VersionFakeService {


	@Autowired
	private HeldRepositoryService heldRepositoryService;

	@Value("${dsa.online.fakes.directory}")
	private String fakesDirectory;

	@Value("${dsa.online.fakes.enabled}")
	private boolean useFakes;

	@Autowired
	private HeldenApi heldenApi;

	private final HeldenService heldenService;

	public VersionFakeService(HeldenService heldenService) {
		this.heldenService = heldenService;
	}

	@PostConstruct
	public void afterInit() {
		log.info("Fakes directory is: " + fakesDirectory);
	}

	//Fakes Versions, but only for ids in given list
	public void fakeVersions(List<BigInteger> heldenIds) {
		if (!useFakes) {
			return;
		}
		File dir = new File(fakesDirectory + "/versionfakes");
		Map<BigInteger, List<File>> mapping = new HashMap<>();
		if (!dir.exists()) {
			log.error("Cant fake versions because directory {} does not exist", dir.getAbsoluteFile());
			return;
		}
		for (File file : dir.listFiles()) {
			BigInteger heldid = new BigInteger(file.getName().split("\\.")[1]);
			mapping.computeIfAbsent(heldid, k -> new ArrayList<>()).add(file);

		}
		mapping.values().forEach(list -> list.sort((one, two) -> {
			Integer firstVersion = Integer.valueOf(one.getName().split("\\.")[0]);
			Integer secondVersion = Integer.valueOf(two.getName().split("\\.")[0]);
			return firstVersion - secondVersion;
		}));

		mapping.entrySet()
				.stream()
				.filter(entry -> heldenIds.contains(entry.getKey()))
				.forEach(entry -> entry.getValue().forEach(this::fakeVersion));
	}


	private void fakeVersion(File file) {
		try {
			ZipFile zipFile = new ZipFile(file);
			int version = Integer.valueOf(file.getName().split("\\.")[0]);
			BigInteger heldid = new BigInteger(file.getName().split("\\.")[1]);
			VersionEntity versionEntity = heldRepositoryService.findVersion(heldid, version);
			ReturnHeldDatenWithEreignisseRequest request = new ReturnHeldDatenWithEreignisseRequest(versionEntity.getId().getHeldid(), null, version);
			InputStream is = zipFile.getInputStream(zipFile.getEntry("daten.xml"));
			Daten fakeDaten = (Daten) JaxbUtil.getUnmarshaller(Daten.class).unmarshal(is);
			Daten cacheDaten = heldenApi.request(request, true).block();
			HeldenUnterschied unterschied = heldenService.calculateUnterschied(fakeDaten, cacheDaten);
			if (unterschied.getEreignis().getEmpty() && unterschied.getGegenstaende().getEmpty()) {
				log.info("Skipping fake version {} {} because it is equal to the current version", heldid, version);
				zipFile.close();
				return;
			}
			Helper.copyFilesToHigherVersion(heldid, version, heldenApi.getCacheHandler());
			fakeHeldenXml(zipFile.getInputStream(zipFile.getEntry("held.xml")), heldid, version);
			fakePdf(zipFile.getInputStream(zipFile.getEntry("held.pdf")), heldid, version);
			fakeDatenXml(zipFile.getInputStream(zipFile.getEntry("daten.xml")), heldid, version);
			zipFile.close();
			versionEntity.getId().setVersion(version + 1);
			heldRepositoryService.saveVersion(versionEntity);
			log.info("Saved fake {} {}", versionEntity.getId().getHeldid(), versionEntity.getId().getVersion());
		} catch (FileNotFoundException e) {
			log.error("Exceptin while faking version", e);
			throw new RuntimeException(e);
		} catch (IOException e) {
			log.error("Exceptin while faking version", e);
			throw new RuntimeException(e);
		} catch (JAXBException e) {
			throw new CorruptXmlException(e);
		}


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
}
