package com.furnicraft.security.auth;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;

@Component
public class AuthenticatedUserProvider {

    public String getEmail() {
        Authentication authentication = getAuthentication();

        if (authentication == null || authentication.getPrincipal() == null) {
            return null;
        }

        return authentication.getPrincipal().toString();
    }

    public UUID getUserId() {
        Authentication authentication = getAuthentication();

        if (authentication == null) {
            return null;
        }

        Object details = authentication.getDetails();

        if (details instanceof Map<?, ?> map) {
            Object userId = map.get("userId");
            if (userId != null) {
                return UUID.fromString(userId.toString());
            }
        }

        return null;
    }

    public String getRole() {
        Authentication authentication = getAuthentication();

        if (authentication == null) {
            return null;
        }

        Object details = authentication.getDetails();

        if (details instanceof Map<?, ?> map) {
            Object role = map.get("role");
            return role == null ? null : role.toString();
        }

        return null;
    }

    private Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }
}