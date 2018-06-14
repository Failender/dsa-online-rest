package de.failender.dsaonline.security;

import de.failender.dsaonline.data.entity.HeldEntity;
import de.failender.dsaonline.data.entity.UserEntity;
import de.failender.dsaonline.exceptions.NotAuthenticatedException;
import de.failender.heldensoftware.api.authentication.TokenAuthentication;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

public class SecurityUtils {


	public static final String CREATE_USER = "CREATE_USER";
	public static final String VIEW_ALL = "VIEW_ALL";
	public static final String EDIT_ALL = "EDIT_ALL";

	public static void checkRight(String right) {

		Collection<? extends GrantedAuthority> authorities = SecurityContextHolder.getContext().getAuthentication().getAuthorities();
		Iterator<? extends GrantedAuthority> it = authorities.iterator();
		while (it.hasNext()) {
			GrantedAuthority grantedAuthority = it.next();
			if (grantedAuthority.getAuthority().equals(right)) {
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
		if (principal instanceof String) {
			throw new NotAuthenticatedException();
		}
	}

	public static UserEntity getCurrentUser() {
		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		if (principal instanceof String) {
			throw new NotAuthenticatedException();
		} else {
			return (UserEntity) principal;
		}
	}

	public static void canCurrentUserViewHeld(HeldEntity held) {
		if (held.isPublic()) {
			return;
		}
		UserEntity user = getCurrentUser();
		if (user.getId() != held.getUserId()) {
			checkRight(SecurityUtils.VIEW_ALL);
		}
	}

	public static void canCurrentUserEditHeld(HeldEntity held) {
		UserEntity user = getCurrentUser();
		if (user.getId() != held.getUserId()) {
			checkRight(SecurityUtils.EDIT_ALL);
		}
	}

	public static TokenAuthentication getAuthentication() {
		checkLogin();
		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		UserEntity user = (UserEntity) principal;
		return new TokenAuthentication(user.getToken());
	}
}
