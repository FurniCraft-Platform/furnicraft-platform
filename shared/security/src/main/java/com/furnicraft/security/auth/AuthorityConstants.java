package com.furnicraft.security.auth;

public final class AuthorityConstants {
    private AuthorityConstants() {}

    public static String role(Role role) {
        return "ROLE_" + role.name();
    }

    public static String permission(Permission permission) {
        return permission.name();
    }
}