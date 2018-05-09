package de.failender.dsaonline.service;

import de.failender.dsaonline.api.HeldenSoftwareAPI;
import de.failender.dsaonline.api.HeldenSoftwareAPIOffline;
import de.failender.dsaonline.api.HeldenSoftwareAPIOnline;
import de.failender.dsaonline.data.entity.UserEntity;
import de.failender.dsaonline.security.SecurityUtils;
import de.failender.heldensoftware.xml.datenxml.Daten;
import de.failender.heldensoftware.xml.heldenliste.Held;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.List;

@Service
public class ApiService {


	@Value("${dsa.heldensoftware.online}")
	private boolean online;


	@Autowired
	private Environment environment;

	@Autowired
	private CachingService cachingService;

	private HeldenSoftwareAPI getApi(String token) {

		if(online) {
			return new HeldenSoftwareAPIOnline(token, environment);
		} else {
			return new HeldenSoftwareAPIOffline(token);
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
		return this.getAllHelden(token);
	}

	public Daten getHeldenDaten(BigInteger heldenid, int version) {
		Daten cache = cachingService.getHeldenDatenCache(heldenid, version);
		if(cache != null) {
			return cache;
		}
		cache = getApi(getToken()).getHeldenDaten(heldenid);
		cachingService.setHeldenDatenCache(heldenid, version, cache);
		return cache;
	}

	public List<Held> getAllHelden(String token) {
		List<Held> cache = cachingService.getAllHeldenCache(token);
		if(cache != null) {
			return cache;
		}
		cache = getApi(getToken()).getAllHelden();
		cachingService.setAllHeldenCache(token, cache);
		return cache;
	}
}
