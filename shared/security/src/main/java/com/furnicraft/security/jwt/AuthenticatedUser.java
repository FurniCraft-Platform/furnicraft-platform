package com.furnicraft.security.jwt;

import java.util.UUID;

public record AuthenticatedUser(
        String username,
        UUID userId
) {
}