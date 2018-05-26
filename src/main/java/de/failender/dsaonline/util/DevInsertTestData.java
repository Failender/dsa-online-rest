package de.failender.dsaonline.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.failender.dsaonline.data.entity.UserEntity;
import de.failender.dsaonline.data.repository.GruppeRepository;
import de.failender.dsaonline.data.repository.HeldRepository;
import de.failender.dsaonline.data.repository.UserRepository;
import de.failender.dsaonline.rest.user.UserRegistration;
import de.failender.dsaonline.security.SecurityUtils;
import de.failender.dsaonline.service.*;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

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

	@Value("${dsa.online.cache.droponstart}")
	private boolean dropCacheOnStart;

	@Override
	public void onApplicationEvent(ApplicationReadyEvent applicationReadyEvent) {
		if(dropCacheOnStart) {
			cachingService.dropCache();
		}

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
						if(userRepository.existsByName(userData.getName())) {
							log.info("User with name {} already exists in database", userData.getName());
							return;
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
			log.error("Error while inserting user data", e);
		}
		SecurityContextHolder.getContext().setAuthentication(null);
		try {
			converterThread.join();
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
		userRepository.findAll().forEach(userHeldenService::updateHeldenForUser);

		log.info("Done inserting dev data");
	}

	//Only works in non jar'ed


	@Data
	public static class UserData {
		private String name;
		private String token;
		private List<String> roles;
		private String gruppe;
	}
}
