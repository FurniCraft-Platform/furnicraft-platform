package com.furnicraft.security.auth;

import com.furnicraft.security.enums.Permission;
import com.furnicraft.security.enums.Role;

public final class AuthorityConstants {
    private AuthorityConstants() {}

    public static String role(Role role) {
        return "ROLE_" + role.name();
    }

    public static String permission(Permission permission) {
        return permission.name();
    }
}