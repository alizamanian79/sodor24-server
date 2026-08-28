package com.app.server.annotation;

import com.app.server.model.User;
import com.app.server.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

@Component("userSecurity")
@RequiredArgsConstructor
public class UserSecurity {

    private final UserRepository userRepository;

    public boolean isSelfOrAdminBySub(Authentication authentication, String targetSub) {
        if (authentication == null || !authentication.isAuthenticated()
                || !(authentication.getPrincipal() instanceof Jwt jwt)) {
            throw new AccessDeniedException("احراز هویت نامعتبر است");
        }

        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (isAdmin) {
            return true;
        }

        String subFromToken = jwt.getSubject();

        User me = userRepository.findUserBySub(subFromToken)
                .orElseThrow(() -> new AccessDeniedException("کاربر یافت نشد"));

        if (!me.getSub().equals(targetSub)) {
            throw new AccessDeniedException("شما اجازه‌ی دسترسی به اطلاعات این کاربر را ندارید");
        }

        return true;
    }
}