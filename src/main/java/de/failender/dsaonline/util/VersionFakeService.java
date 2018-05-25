package de.failender.dsaonline.util;

import de.failender.dsaonline.data.entity.HeldEntity;
import de.failender.dsaonline.data.repository.HeldRepository;
import de.failender.dsaonline.service.CachingService;
import de.failender.heldensoftware.xml.datenxml.Daten;
import de.failender.heldensoftware.xml.datenxml.Ereignis;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.util.*;

@Service
@Slf4j
public class VersionFakeService {


	@Autowired
	private CachingService cachingService;

	@Autowired
	private HeldRepository heldRepository;

	//Fakes Versions, but only for ids in given list
	public void fakeVersions(List<BigInteger> heldenIds) {
		File dir = new File("fakes/versionfakes");
		Map<BigInteger, List<File>> mapping = new HashMap<>();
		if(!dir.exists()) {
			log.error("Cant fake versions because directory {} does not exist", dir.getAbsoluteFile());
			return;
		}
		for(File file: dir.listFiles()) {
			BigInteger heldid = new BigInteger(file.getName().split("\\.")[1]);
			mapping.computeIfAbsent(heldid, k -> new ArrayList<>()).add(file);

		}
		mapping.values().forEach(list -> list.sort((one,two) -> {
			Integer firstVersion = Integer.valueOf(one.getName().split("\\.")[0]);
			Integer secondVersion = Integer.valueOf(two.getName().split("\\.")[0]);
			return firstVersion-secondVersion;
		}));
		mapping.entrySet()
				.stream()
				.filter(entry -> heldenIds.contains(entry.getKey()))
				.forEach(entry -> entry.getValue().forEach(this::fakeVersion));
	}

	public void fakeVersions() {

		File dir = new File("fakes/versionfakes");
		Map<BigInteger, List<File>> mapping = new HashMap<>();
		for(File file: dir.listFiles()) {
			BigInteger heldid = new BigInteger(file.getName().split("\\.")[1]);
			mapping.computeIfAbsent(heldid, k -> new ArrayList<>()).add(file);

		}
		mapping.values().forEach(list -> list.sort((one,two) -> {
			Integer firstVersion = Integer.valueOf(one.getName().split("\\.")[0]);
			Integer secondVersion = Integer.valueOf(two.getName().split("\\.")[0]);
			return firstVersion-secondVersion;
		}));
		mapping.entrySet().forEach(entry -> entry.getValue().forEach(this::fakeVersion));

	}

	private void fakeVersion(File file) {
		File xmlFile = new File(file.getParentFile().getParentFile() + "/versionfakes_helden", file.getName());
		if(!xmlFile.exists()) {
			log.error("Cant fake version {} because no corresponding xml file found", file.getName());
			return;
		}
		String xml;
		try {
			xml = FileUtils.readFileToString(xmlFile, "UTF-8");
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		int version = Integer.valueOf(file.getName().split("\\.")[0]);
		BigInteger heldid = new BigInteger(file.getName().split("\\.")[1]);

		Unmarshaller unmarshaller = JaxbUtil.getUnmarshaller(Daten.class);
		try {
			Daten daten = (Daten) unmarshaller.unmarshal(file);
			this.fakeVersion(daten, heldid, version, xml);
		} catch (JAXBException e) {
			throw new RuntimeException(e);
		}
	}

	private void fakeVersion(Daten daten, BigInteger heldid, int version, String xml ) {

		log.info("Faking version {} for held {}", version, heldid);
		if(version== 1) {
			Optional<HeldEntity> heldEntityOptional = this.heldRepository.findByIdIdAndIdVersion(heldid, version);
			if(!heldEntityOptional.isPresent()) {
				log.error("Held {} mit version {} konnte nicht gefunden werden");
				return;
			}
			HeldEntity heldEntity = heldEntityOptional.get();
			List<Ereignis> ereignis = daten.getEreignisse().getEreignis();
			heldEntity.setCreatedDate(new Date(ereignis.get(ereignis.size()-1).getDate()));
			heldEntity.setPdfCached(false);
			cachingService.purgePdfCacheFor(heldEntity.getId().getId(), heldEntity.getVersion());
			this.heldRepository.save(heldEntity);
			cachingService.setHeldenCache(heldEntity.getId().getId(), heldEntity.getVersion(), daten, xml);
		} else {
			HeldEntity heldEntity = this.heldRepository.findByIdIdAndIdVersion(heldid, version -1).get();
			heldEntity.setActive(false);
			this.heldRepository.save(heldEntity);
			heldEntity = heldEntity.clone();
			heldEntity.setVersion(version);
			heldEntity.setActive(true);
			List<Ereignis> ereignis = daten.getEreignisse().getEreignis();
			heldEntity.setCreatedDate(new Date(ereignis.get(ereignis.size()-1).getDate()));
			System.out.println(version + ":   " + heldEntity.getCreatedDate().getTime());
			this.heldRepository.save(heldEntity);
			cachingService.setHeldenCache(heldEntity.getId().getId(), heldEntity.getVersion(), daten, xml);
		}
	}
}
