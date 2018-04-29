package de.failender.dsaonline.service;

import de.failender.dsaonline.api.HeldenSoftwareAPI;
import de.failender.dsaonline.api.HeldenSoftwareAPIOffline;
import de.failender.dsaonline.api.HeldenSoftwareAPIOnline;
import de.failender.dsaonline.data.entity.UserEntity;
import de.failender.dsaonline.security.SecurityUtils;
import de.failender.heldensoftware.xml.datenxml.Daten;
import de.failender.heldensoftware.xml.heldenliste.Held;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.List;

@Service
public class ApiService {


	@Value("${dsa.heldensoftware.online}")
	private boolean online;

	private HeldenSoftwareAPI getApi() {
		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		SecurityUtils.checkLogin();
		if(online) {
			UserEntity user = (UserEntity) principal;
			return new HeldenSoftwareAPIOnline(user.getToken());
		} else {
			return new HeldenSoftwareAPIOffline();
		}
	}

	public List<Held> getAllHelden() {
		return getApi().getAllHelden();
	}

	public Daten getHeldenDaten(BigInteger heldenid) {
		return getApi().getHeldenDaten(heldenid);
	}

	public String getHeldXml(BigInteger heldenid) {
		return getApi().getHeldXml(heldenid);
	}
}
