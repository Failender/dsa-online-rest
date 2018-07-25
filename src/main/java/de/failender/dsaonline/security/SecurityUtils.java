package de.failender.dsaonline.security;

import de.failender.dsaonline.data.entity.HeldEntity;
import de.failender.dsaonline.data.entity.UserEntity;
import de.failender.dsaonline.data.repository.UserRepository;
import de.failender.dsaonline.exceptions.NotAuthenticatedException;
import de.failender.heldensoftware.api.authentication.TokenAuthentication;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class SecurityUtils {


	public static final String CREATE_USER = "CREATE_USER";
	public static final String VIEW_ALL = "VIEW_ALL";
	public static final String EDIT_ALL = "EDIT_ALL";
	public static final String MEISTER = "MEISTER";

	@Autowired
	private UserRepository userRepository;

	public static void checkRight(String right) {

		if (hasRight(right)) {
			return;
		}
		log.error("Access right not found " + right);
		for (StackTraceElement stackTraceElement : Thread.currentThread().getStackTrace()) {
			log.error(stackTraceElement.toString());
		}

		throw new AccessDeniedException(right);
	}

	public static boolean hasRight(String right) {
		Collection<? extends GrantedAuthority> authorities = SecurityContextHolder.getContext().getAuthentication().getAuthorities();
		Iterator<? extends GrantedAuthority> it = authorities.iterator();
		while (it.hasNext()) {
			GrantedAuthority grantedAuthority = it.next();
			if (grantedAuthority.getAuthority().equals(right)) {
				return true;
			}
		}
		return false;
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

	public static boolean isLoggedIn() {
		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		if (principal instanceof String) {
			return false;
		}
		return true;
	}

	public static UserEntity getCurrentUser() {
		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		if (principal instanceof String) {
			throw new NotAuthenticatedException();
		} else {
			return (UserEntity) principal;
		}
	}

	public void canCurrentUserViewHeld(HeldEntity held) {
		if (held.isPublic()) {
			return;
		}
		UserEntity user = getCurrentUser();
		if(user.getId() == held.getUserId()) {
			return;
		}
		if(hasRight(SecurityUtils.VIEW_ALL)) {
			return;
		}
		checkIsUserMeisterForGruppe(held.getGruppe().getId());

//		throw new AccessDeniedException("Cant view held" + held.getName());
	}

	public void checkIsUserMeisterForGruppe(int gruppeid) {
		UserEntity user = getCurrentUser();
		if(hasRight(SecurityUtils.MEISTER)) {
			List<Integer> gruppen = userRepository.getMeisterGruppen(user.getId());
			if(gruppen.contains(gruppeid)) {
				return;
			}
		}
		log.error("User is no master for group {}", gruppeid);
		throw new AccessDeniedException("");
	}

	public static void canCurrentUserEditHeld(HeldEntity held) {
		UserEntity user = getCurrentUser();
		if (user.getId() != held.getUserId()) {
			checkRight(SecurityUtils.EDIT_ALL);
		}
	}

	public static TokenAuthentication getAuthentication() {
		if(!isLoggedIn()) {
			return null;
		}
		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		UserEntity user = (UserEntity) principal;
		return new TokenAuthentication(user.getToken());
	}
}
