package com.furnicraft.user.security;

import com.furnicraft.common.exception.BaseException;
import com.furnicraft.common.exception.ErrorCode;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class CurrentUserService {

    public UUID getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || authentication.getPrincipal() == null) {
            throw new BaseException("Authenticated user not found", ErrorCode.INVALID_CREDENTIALS);
        }

        Object principal = authentication.getPrincipal();

        if (principal instanceof UUID uuid) {
            return uuid;
        }

        if (principal instanceof String value) {
            return UUID.fromString(value);
        }

        throw new BaseException("Invalid authenticated principal", ErrorCode.INVALID_CREDENTIALS);
    }
}