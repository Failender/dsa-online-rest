package de.failender.dsaonline.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.failender.dsaonline.data.entity.UserEntity;
import de.failender.dsaonline.data.repository.GruppeRepository;
import de.failender.dsaonline.data.repository.UserRepository;
import de.failender.dsaonline.rest.user.UserRegistration;
import de.failender.dsaonline.security.SecurityUtils;
import de.failender.dsaonline.service.UserHeldenService;
import de.failender.dsaonline.service.UserService;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
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

	@Autowired
	private UserHeldenService userHeldenService;

	@Autowired
	private GruppeRepository gruppeRepository;


	@Override
	public void onApplicationEvent(ApplicationReadyEvent applicationReadyEvent) {
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
						String gruppe = null;
						if(userData.getGruppe() != null) {
							gruppe = userData.getGruppe();
						} else {
							gruppe = this.gruppeRepository.findAll().get(0).getName();
						}
						UserRegistration userRegistration = new UserRegistration(userData.getName(), null, userData.getToken(), gruppe);
						UserEntity userEntity = this.userService.registerUser(userRegistration);
						userData.roles.forEach(role -> {
							this.userService.addUserRole(userEntity, role);
						});
						userHeldenService.updateHeldenForUser(userEntity);
					}
			);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}



		SecurityContextHolder.getContext().setAuthentication(null);
	}


	private String randomName() {
		byte[] array = new byte[7]; // length is bounded by 7
		new Random().nextBytes(array);
		String generatedString = new String(array, Charset.forName("UTF-8"));
		return generatedString;
	}

	@Data
	public static class UserData {
		private String name;
		private String token;
		private List<String> roles;
		private String gruppe;
	}
}
