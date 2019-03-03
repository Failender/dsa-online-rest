package de.failender.dsaonline.util;

import de.failender.dsaonline.data.entity.UserEntity;
import de.failender.dsaonline.security.SecurityUtils;
import de.failender.heldensoftware.api.authentication.TokenAuthentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class AuthenticationUtil {

    public static TokenAuthentication getAuthentication() {
        if (!SecurityUtils.isLoggedIn()) {
            return null;
        }
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserEntity user = (UserEntity) principal;
        if (user.getToken() == null) {
            return null;
        }
        return new TokenAuthentication(user.getToken());
    }
}
