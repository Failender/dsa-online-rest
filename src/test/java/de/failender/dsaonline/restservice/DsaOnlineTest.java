package de.failender.dsaonline.restservice;

import de.failender.dsaonline.DsaOnlineRest;
import de.failender.dsaonline.data.entity.UserEntity;
import de.failender.dsaonline.data.repository.UserRepository;
import org.flywaydb.test.FlywayTestExecutionListener;
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

import java.util.List;
import java.util.stream.Collectors;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = DsaOnlineRest.class)
@ActiveProfiles(profiles="test")
@TestExecutionListeners({DependencyInjectionTestExecutionListener.class, FlywayTestExecutionListener.class})
public abstract class DsaOnlineTest {

	@Autowired
	private UserRepository userRepository;

	@Before
	public void setup() {
		UserEntity user = this.userRepository.findByName("Failender");
		SecurityContextHolder.getContext().setAuthentication(
				new UsernamePasswordAuthenticationToken(user, null, loadUserRights(user.getId()))
		);
	}

	private List<SimpleGrantedAuthority> loadUserRights(int userId) {
		return userRepository.getUserRights(userId).stream().map(right -> new SimpleGrantedAuthority(right)).collect(Collectors.toList());
	}


}
