package com.furnicraft.security.auth;

import com.furnicraft.security.jwt.AuthenticatedUser;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component("authz")
public class AuthorizationService {

    public boolean isCurrentUser(UUID userId) {
        if (userId == null) {
            return false;
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null) {
            return false;
        }

        Object principal = authentication.getPrincipal();

        if (principal instanceof AuthenticatedUser authenticatedUser) {
            return userId.equals(authenticatedUser.userId());
        }

        return false;
    }
}