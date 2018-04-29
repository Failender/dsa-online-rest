package de.failender.dsaonline.util;

import de.failender.dsaonline.data.entity.UserEntity;
import de.failender.dsaonline.data.repository.UserRepository;
import de.failender.dsaonline.rest.user.UserRegistration;
import de.failender.dsaonline.security.SecurityUtils;
import de.failender.dsaonline.service.UserService;
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

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
@Profile("dev")
public class DevInsertTestData implements ApplicationListener<ApplicationReadyEvent> {

	@Autowired
	private UserService userService;

	@Autowired
	private UserRepository userRepository;

	@Value("${dsa.heldensoftware.tokens}")
	private List<String> tokens;

	@Override
	public void onApplicationEvent(ApplicationReadyEvent applicationReadyEvent) {
		List<GrantedAuthority> fakeRights = new ArrayList<>();
		fakeRights.add(new SimpleGrantedAuthority(SecurityUtils.CREATE_USER));
		SecurityContextHolder.getContext().setAuthentication(
				new UsernamePasswordAuthenticationToken(userRepository.findById(1).get(), null, fakeRights));
		tokens.forEach(token -> {
			UserRegistration userRegistration = new UserRegistration();
			userRegistration.setToken(token);
			userRegistration.setGruppe("TestGruppe");
			userRegistration.setName(randomName());
			UserEntity user = userService.registerUser(userRegistration);


		});


		SecurityContextHolder.getContext().setAuthentication(null);
	}


	private String randomName() {
		byte[] array = new byte[7]; // length is bounded by 7
		new Random().nextBytes(array);
		String generatedString = new String(array, Charset.forName("UTF-8"));
		return generatedString;
	}
}
