package de.failender.dsaonline.security;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class AuthorizationFilter extends BasicAuthenticationFilter {


	private final AuthorizationService authorizationService;

	public static final String USERNAME = "X-USER";
	public static final String PASSWORD = "X-PASSWORD";

	public AuthorizationFilter(AuthenticationManager auth, AuthorizationService authorizationService) {
		super(auth);
		this.authorizationService = authorizationService;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
		authorizationService.authenticate(request.getHeader(PASSWORD), request.getHeader(USERNAME));
		chain.doFilter(request, response);


	}


}
