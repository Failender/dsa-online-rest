package de.failender.dsaonline.service;

import de.failender.dsaonline.exceptions.CorruptXmlException;
import de.failender.dsaonline.security.SecurityUtils;
import de.failender.dsaonline.util.JaxbUtil;
import de.failender.heldensoftware.xml.datenxml.Daten;
import de.failender.heldensoftware.xml.heldenliste.Held;
import de.failender.heldensoftware.xml.heldenliste.Helden;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.File;
import java.math.BigInteger;
import java.util.List;

@Service
public class CachingService {

	@Value("${dsa.online.cache.directory}")
	private String cacheDirectoryString;

	@Value("${dsa.online.cache.duration}")
	private int cacheDuration;

	private File cacheDirectory;

	@Autowired
	private UserHeldenService userHeldenService;

	@PostConstruct
	public void init() {
		cacheDirectory = new File(cacheDirectoryString);
		if(!cacheDirectory.exists()) {
			cacheDirectory.mkdirs();
		}
	}

	public Daten getHeldenDatenCache(BigInteger heldid, int version) {
		File file = getHeldenDatenCacheFile(heldid, version);
		if(file.exists()) {
			long age = System.currentTimeMillis() - file.lastModified();
			if(age > cacheDuration){
				return null;
			}
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
			return JaxbUtil.heldenFromFile(file);
		}
		return null;
	}

	public void setAllHeldenCache(String token, List<Held> helden) {
		this.userHeldenService.updateHeldenForUser(SecurityUtils.getCurrentUser());
		Helden xmlHelden = new Helden();
		xmlHelden.setHeld(helden);
		Marshaller marshaller = JaxbUtil.getMarshaller(Helden.class);
		try {
			marshaller.marshal(xmlHelden, getAllHeldenCacheFile(token));
		} catch (JAXBException e) {
			throw new CorruptXmlException(e);
		}
	}

	public void setHeldenDatenCache(BigInteger heldid, int version, Daten daten){
		Marshaller marshaller = JaxbUtil.getMarshaller(Daten.class);
		try {
			marshaller.marshal(daten, getHeldenDatenCacheFile(heldid, version));
		} catch(JAXBException e) {
			throw new CorruptXmlException(e);
		}
	}

	private File getAllHeldenCacheFile(String token) {
		return new File(cacheDirectory, token+".xml");
	}
	private File getHeldenDatenCacheFile(BigInteger id, int version) {
		return new File(cacheDirectory, version + "." + id+".xml");
	}


}
