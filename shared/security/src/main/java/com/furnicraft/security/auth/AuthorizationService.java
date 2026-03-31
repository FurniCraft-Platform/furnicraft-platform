package com.furnicraft.security.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component("authz")
@RequiredArgsConstructor
public class AuthorizationService {

    private final AuthenticatedUserProvider authenticatedUserProvider;

    public boolean isCurrentUser(UUID userId) {
        if (userId == null) {
            return false;
        }

        UUID currentUserId = authenticatedUserProvider.getUserId();
        return currentUserId != null && currentUserId.equals(userId);
    }
}