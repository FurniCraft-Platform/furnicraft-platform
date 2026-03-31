package com.furnicraft.user.security;

import com.furnicraft.common.exception.BaseException;
import com.furnicraft.common.exception.ErrorCode;
import com.furnicraft.security.auth.AuthenticatedUserProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CurrentUserService {

    private final AuthenticatedUserProvider authenticatedUserProvider;

    public UUID getCurrentUserId() {
        UUID userId = authenticatedUserProvider.getUserId();

        if (userId == null) {
            throw new BaseException("Authenticated user not found", ErrorCode.INVALID_CREDENTIALS);
        }

        return userId;
    }

    public String getCurrentUserEmail() {
        String email = authenticatedUserProvider.getEmail();

        if (email == null || email.isBlank()) {
            throw new BaseException("Authenticated user email not found", ErrorCode.INVALID_CREDENTIALS);
        }

        return email;
    }

    public String getCurrentUserRole() {
        String role = authenticatedUserProvider.getRole();

        if (role == null || role.isBlank()) {
            throw new BaseException("Authenticated user role not found", ErrorCode.INVALID_CREDENTIALS);
        }

        return role;
    }
}