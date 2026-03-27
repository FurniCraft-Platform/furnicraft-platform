package com.furnicraft.user.security;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service("userAuth")
@RequiredArgsConstructor
public class UserAuthorizationService {

    private final CurrentUserService currentUserService;

    public boolean isCurrentUser(UUID userId) {
        return currentUserService.getCurrentUserId().equals(userId);
    }
}