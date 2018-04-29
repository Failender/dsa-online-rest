package de.failender.dsaonline.security;

import de.failender.dsaonline.exceptions.NotAuthenticatedException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

public class SecurityUtils {


	public static final String CREATE_USER = "CREATE_USER";

	public static void checkRight(String right) {

		Collection<? extends GrantedAuthority> authorities = SecurityContextHolder.getContext().getAuthentication().getAuthorities();
		Iterator<? extends GrantedAuthority> it = authorities.iterator();
		while( it.hasNext()) {
			GrantedAuthority grantedAuthority = it.next();
			if(grantedAuthority.getAuthority().equals(right)) {
				return;
			}
		}
		throw new AccessDeniedException(right);
	}

	public static List<String> getAuthorities() {
		checkLogin();
		return SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream().map(auth -> auth.getAuthority()).collect(Collectors.toList());
	}

	public static void checkLogin() {
		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		if(principal instanceof String){
			throw new NotAuthenticatedException();
		}
	}
}
