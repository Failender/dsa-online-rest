package de.failender.dsaonline.security;

import de.failender.dsaonline.data.entity.UserEntity;
import de.failender.dsaonline.data.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AuthorizationService {

	@Autowired
	private UserRepository userRepository;

	public void authenticate(String password, String username) {

		if(username == null) {

			return;
		}
		UserEntity user = this.userRepository.findByName(username);
		if(user == null || user.getPassword() != null && !user.getPassword().equals(password)) {
			return;
		}
		SecurityContextHolder.getContext().setAuthentication(
				new UsernamePasswordAuthenticationToken(user, null, loadUserRights(user.getId()))
		);
	}

	public void authenticate(RestAuthentication authentication) {
		authenticate(authentication.getXPassword(), authentication.getXUser());
	}

	private List<SimpleGrantedAuthority> loadUserRights(int userId) {
		return userRepository.getUserRights(userId).stream().map(right -> new SimpleGrantedAuthority(right)).collect(Collectors.toList());
	}

}
