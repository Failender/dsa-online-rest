package de.failender.dsaonline.service;

import de.failender.dsaonline.data.repository.UserRepository;
import de.failender.dsaonline.exceptions.CorruptXmlException;
import de.failender.dsaonline.util.JaxbUtil;
import de.failender.heldensoftware.xml.datenxml.Daten;
import de.failender.heldensoftware.xml.heldenliste.Held;
import de.failender.heldensoftware.xml.heldenliste.Helden;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.util.List;

@Service
@Slf4j
public class CachingService {

	@Value("${dsa.online.cache.directory}")
	private String cacheDirectoryString;

	@Value("${dsa.online.cache.duration}")
	private int cacheDuration;

	@Autowired
	private UserRepository userRepository;

	private File xmlCacheDirectory;
	private File datenCacheDirectory;

	@Autowired
	private UserHeldenService userHeldenService;

	@PostConstruct
	public void init() {

		xmlCacheDirectory = new File(cacheDirectoryString + "/xml");
		if(!xmlCacheDirectory.exists()) {
			xmlCacheDirectory.mkdirs();
		}
		datenCacheDirectory= new File(cacheDirectoryString + "/daten");
		if(!datenCacheDirectory.exists()) {
			datenCacheDirectory.mkdirs();
		}
	}

	public Daten getHeldenDatenCache(BigInteger heldid, int version) {
		File file = getHeldenDatenCacheFile(heldid, version);
		if(file.exists()) {
			long age = System.currentTimeMillis() - file.lastModified();
			if(age > cacheDuration){
				return null;
			}
			log.info("Found cache for heldendaten {}.{}", version, heldid);
			return JaxbUtil.datenFromFile(file);
		}
		return null;
	}

	public List<Held> getAllHeldenCache(String token) {
		File file = getAllHeldenCacheFile(token);
		if(file.exists()) {

			long age = System.currentTimeMillis() - file.lastModified();
			if(age > cacheDuration){
				return null;
			}
			log.info("Found cache for allhelden with token {}", token);
			return JaxbUtil.heldenFromFile(file);
		}
		return null;
	}

	public void setAllHeldenCache(String token, List<Held> helden) {

		Helden xmlHelden = new Helden();
		xmlHelden.setHeld(helden);
		Marshaller marshaller = JaxbUtil.getMarshaller(Helden.class);
		try {
			File out = getAllHeldenCacheFile(token);
			marshaller.marshal(xmlHelden, getAllHeldenCacheFile(token));
		} catch (JAXBException e) {
			throw new CorruptXmlException(e);
		}
		this.userHeldenService.updateHeldenForUser(this.userRepository.findByToken(token));
	}

	public void purgeAllHeldenCache(String token) {
		File file = getAllHeldenCacheFile(token);
		if(file.exists()) {
			if(!file.delete()) {
				log.error("Failed to purgeAllHeldenCache for token " + token);
			}
		}
	}

	public void setHeldenCache(BigInteger heldid, int version, Daten daten, String xml) {
		setHeldenDatenCache(heldid, version, daten);
		setHeldenXmlCache(heldid, version, xml);
	}

	private void setHeldenDatenCache(BigInteger heldid, int version, Daten daten){
		Marshaller marshaller = JaxbUtil.getMarshaller(Daten.class);
		try {
			marshaller.marshal(daten, getHeldenDatenCacheFile(heldid, version));
		} catch(JAXBException e) {
			throw new CorruptXmlException(e);
		}
	}

	private void setHeldenXmlCache(BigInteger heldid, int version, String xml) {
		try {
			FileUtils.writeStringToFile(getHeldenXmlCacheFile(heldid, version), xml, "UTF-8");
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public void dropCache() {
		log.warn("DROPPING CACHE!");
		try {
			FileUtils.cleanDirectory(xmlCacheDirectory);
			FileUtils.cleanDirectory(datenCacheDirectory);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private File getAllHeldenCacheFile(String token) {
		return new File(datenCacheDirectory, token+".xml");
	}
	private File getHeldenDatenCacheFile(BigInteger id, int version) {
		return new File(datenCacheDirectory, version + "." + id+".xml");
	}

	private File getHeldenXmlCacheFile(BigInteger id, int version) {
		return new File(xmlCacheDirectory, version + "." + id+".xml");
	}


}
