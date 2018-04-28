package de.failender.dsaonline.security;

import de.failender.dsaonline.data.entity.UserEntity;
import de.failender.dsaonline.data.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;

public class AuthorizationFilter extends BasicAuthenticationFilter {


	private final UserRepository userRepository;

	public static final String USERNAME = "X-USER";
	public static final String PASSWORD = "X-PASSWORD";

	public AuthorizationFilter(AuthenticationManager auth, UserRepository userRepository) {
		super(auth);
		this.userRepository = userRepository;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
		String username = request.getHeader(USERNAME);
		if(username == null) {
			chain.doFilter(request, response);
			return;
		}
		String password = request.getHeader(PASSWORD);
		UserEntity user = this.userRepository.findByName(username);
		if(user == null || user.getPassword() != null && !user.getPassword().equals(password)) {
			chain.doFilter(request, response);
			return;
		}
		SecurityContextHolder.getContext().setAuthentication(
				new UsernamePasswordAuthenticationToken(user, null, Collections.EMPTY_LIST)
		);
		chain.doFilter(request, response);
	}
}
