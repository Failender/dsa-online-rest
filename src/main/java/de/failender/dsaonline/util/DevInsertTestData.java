package de.failender.dsaonline.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.failender.dsaonline.data.entity.HeldEntity;
import de.failender.dsaonline.data.entity.UserEntity;
import de.failender.dsaonline.data.repository.GruppeRepository;
import de.failender.dsaonline.data.repository.HeldRepository;
import de.failender.dsaonline.data.repository.UserRepository;
import de.failender.dsaonline.rest.user.UserRegistration;
import de.failender.dsaonline.security.SecurityUtils;
import de.failender.dsaonline.service.*;
import de.failender.heldensoftware.xml.datenxml.Daten;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Profile("dev")
@Slf4j
public class DevInsertTestData implements ApplicationListener<ApplicationReadyEvent> {

	@Autowired
	private UserService userService;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private UserHeldenService userHeldenService;

	@Autowired
	private HeldRepository heldRepository;

	@Autowired
	private GruppeRepository gruppeRepository;

	@Autowired
	private ApiService apiService;

	@Autowired
	private CachingService cachingService;

	@Autowired
	private ConvertingService convertingService;

	@Override
	public void onApplicationEvent(ApplicationReadyEvent applicationReadyEvent) {
		cachingService.dropCache();
		Thread converterThread = new Thread(new FileConvertingRunnable(convertingService));
		converterThread.run();
		log.info("Starting to insert dev data");
		List<GrantedAuthority> fakeRights = new ArrayList<>();
		fakeRights.add(new SimpleGrantedAuthority(SecurityUtils.CREATE_USER));
		SecurityContextHolder.getContext().setAuthentication(
				new UsernamePasswordAuthenticationToken(userRepository.findById(1).get(), null, fakeRights));
		InputStream is = DevInsertTestData.class.getClassLoader().getResourceAsStream("user.json");
		ObjectMapper om = new ObjectMapper();
		try {
			List<UserData> data = om.readValue(is, new TypeReference<List<UserData>>(){});
			data.forEach(
					userData -> {
						if(userData.getToken() == null) {
							System.out.println("Token is null. WTF");
						}
						String gruppe = null;
						if(userData.getGruppe() != null) {
							gruppe = userData.getGruppe();
						} else {
							gruppe = this.gruppeRepository.findAll().get(0).getName();
						}
						UserRegistration userRegistration = new UserRegistration(userData.getName(), null, userData.getToken(), gruppe);
						UserEntity userEntity = this.userService.registerUser(userRegistration);
						userData.roles.forEach(role -> this.userService.addUserRole(userEntity, role));
					}
			);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		SecurityContextHolder.getContext().setAuthentication(null);
		try {
			converterThread.join();
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
		fakeVersions();

		log.info("Done inserting dev data");
	}

	//Only works in non jar'ed
	private void fakeVersions() {

		File dir = new File("src/main/resources/api/offline/versionfakes");
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

		int version = Integer.valueOf(file.getName().split("\\.")[0]);
		BigInteger heldid = new BigInteger(file.getName().split("\\.")[1]);
		Unmarshaller unmarshaller = JaxbUtil.getUnmarshaller(Daten.class);
		try {
			System.out.println(file.getAbsoluteFile());
			Daten daten = (Daten) unmarshaller.unmarshal(file);
			this.fakeVersion(daten, heldid, version);
		} catch (JAXBException e) {
			throw new RuntimeException(e);
		}
	}

	private void fakeVersion(Daten daten, BigInteger heldid, int version ) {


		if(version== 1) {
			HeldEntity heldEntity = this.heldRepository.findByIdIdAndIdVersion(heldid, version).get();

			cachingService.setHeldenDatenCache(heldEntity.getId().getId(), heldEntity.getVersion(), daten);
		} else {
			HeldEntity heldEntity = this.heldRepository.findByIdIdAndIdVersion(heldid, version -1).get();
			heldEntity.setActive(false);
			this.heldRepository.save(heldEntity);
			heldEntity = heldEntity.clone();
			heldEntity.setVersion(version);
			heldEntity.setActive(true);
			this.heldRepository.save(heldEntity);
			cachingService.setHeldenDatenCache(heldEntity.getId().getId(), heldEntity.getVersion(), daten);
		}
	}

	@Data
	public static class UserData {
		private String name;
		private String token;
		private List<String> roles;
		private String gruppe;
	}
}
