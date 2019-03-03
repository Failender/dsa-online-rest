package de.failender.dsaonline.restservice;

import de.failender.dsaonline.DsaOnlineRest;
import de.failender.dsaonline.data.entity.UserEntity;
import de.failender.dsaonline.data.repository.UserRepository;
import org.apache.commons.io.FileUtils;
import org.flywaydb.test.FlywayTestExecutionListener;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = DsaOnlineRest.class)
@ActiveProfiles(profiles="test")
@TestExecutionListeners({DependencyInjectionTestExecutionListener.class, FlywayTestExecutionListener.class})
public abstract class DsaOnlineTest {

	@Autowired
	protected UserRepository userRepository;

	@Before
	public void setup() throws IOException {
		authenticate("Admin");

	}

	private List<SimpleGrantedAuthority> loadUserRights(int userId) {
		return userRepository.getUserRights(userId).stream().map(right -> new SimpleGrantedAuthority(right)).collect(Collectors.toList());
	}

	protected InputStream getResource(String path) {
		return DsaOnlineTest.class.getClassLoader().getResourceAsStream(path);
	}

	protected void authenticate(String username) {
		if(username == null) {
			SecurityContextHolder.getContext().setAuthentication(null);
			return;
		}
		UserEntity user = this.userRepository.findByName(username);
		SecurityContextHolder.getContext().setAuthentication(
				new UsernamePasswordAuthenticationToken(user, null, loadUserRights(user.getId()))
		);
	}


}
