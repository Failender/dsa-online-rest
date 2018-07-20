package de.failender.dsaonline.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.failender.dsaonline.data.repository.UserRepository;
import de.failender.dsaonline.security.SecurityUtils;
import de.failender.dsaonline.service.UserHeldenService;
import de.failender.dsaonline.service.UserService;
import de.failender.heldensoftware.api.HeldenApi;
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
	private HeldenApi heldenApi;

	@Value("${dsa.online.cache.droponstart}")
	private boolean dropCacheOnStart;

	@Override
	public void onApplicationEvent(ApplicationReadyEvent applicationReadyEvent) {
		log.info("Starting to insert dev data");
		List<GrantedAuthority> fakeRights = new ArrayList<>();
		fakeRights.add(new SimpleGrantedAuthority(SecurityUtils.CREATE_USER));
		SecurityContextHolder.getContext().setAuthentication(
				new UsernamePasswordAuthenticationToken(userRepository.findById(1).get(), null, fakeRights));
		InputStream is = DevInsertTestData.class.getClassLoader().getResourceAsStream("user.json");
		ObjectMapper om = new ObjectMapper();
		try {
			List<UserData> data = om.readValue(is, new TypeReference<List<UserData>>() {
			});
			userService.createUsers(data);
		} catch (Exception e) {
			log.error("Error while inserting user data", e);
		}
		SecurityContextHolder.getContext().setAuthentication(null);
		log.info("Done inserting dev data");
	}


	@Data
	public static class UserData {
		public String name;
		public String token;
		public List<String> roles;
		public List<String> meister;
		public String gruppe;
		public String password;
	}
}
