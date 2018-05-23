package de.failender.dsaonline.service;

import de.failender.dsaonline.api.HeldenSoftwareAPI;
import de.failender.dsaonline.api.HeldenSoftwareAPIOnline;
import de.failender.dsaonline.data.entity.UserEntity;
import de.failender.dsaonline.security.SecurityUtils;
import de.failender.heldensoftware.xml.datenxml.Daten;
import de.failender.heldensoftware.xml.datenxml.Ereignis;
import de.failender.heldensoftware.xml.heldenliste.Held;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.Collections;
import java.util.List;

@Service
@Slf4j
public class ApiService {


	@Value("${dsa.heldensoftware.online}")
	private boolean online;


	@Autowired
	private Environment environment;

	@Autowired
	private CachingService cachingService;

	private HeldenSoftwareAPI getApi(String token) {
		log.info("Fetching api for token {}", token);
		if(online) {
			return new HeldenSoftwareAPIOnline(token, environment);
		} else {
			throw new UnsupportedOperationException("Offline api is not supported, use caching instead");
//			return new HeldenSoftwareAPIOffline(token);
		}
	}

	private String getToken() {
		SecurityUtils.checkLogin();
		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		UserEntity user = (UserEntity) principal;
		return user.getToken();
	}

	public List<Held> getAllHelden() {
		String token = getToken();
		if(token == null) {
			log.info("Triing to fetch helden with null token");
			return Collections.EMPTY_LIST;
		}
		return this.getAllHelden(token);
	}

	public Daten getHeldenDaten(BigInteger heldenid, int version) {
		return getHeldenDaten(heldenid, version, getToken());
	}

	public Daten getHeldenDaten(BigInteger heldenid, int version, String token) {
		Daten cache = cachingService.getHeldenDatenCache(heldenid, version);
		if(cache != null) {
			return cache;
		}
		HeldenSoftwareAPI api = getApi(token);
		cache = api.getHeldenDaten(heldenid);
		fixDaten(cache);
		cachingService.setHeldenCache(heldenid, version, cache, api.getHeldXml(heldenid));
		return cache;
	}

	private void fixDaten(Daten daten) {
		List<Ereignis> ereignisse = daten.getEreignisse().getEreignis();
		Ereignis ereignis = ereignisse.get(ereignisse.size()-1);
		if(ereignis.getAktion().equals("Änderungskontrolle")) {
			ereignisse.remove(ereignisse.size()-1);
		}
	}

	public List<Held> getAllHelden(String token) {
		return getAllHelden(token, true);
	}

	public List<Held> getAllHelden(String token, boolean useCache) {
		if(token == null) {
			log.error("token for fetching all helden is null!");
		}
		List<Held> cache = useCache? cachingService.getAllHeldenCache(token): null;
		if(cache != null) {
			return cache;
		}
		cache = getApi(token).getAllHelden();
		cachingService.setAllHeldenCache(token, cache);
		return cache;
	}

	public void purgeAllHeldenCache(String token) {
		this.cachingService.purgeAllHeldenCache(token);
	}
}
