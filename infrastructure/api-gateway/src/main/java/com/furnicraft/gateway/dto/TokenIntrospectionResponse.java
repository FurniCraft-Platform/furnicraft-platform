package com.furnicraft.gateway.dto;

import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class TokenIntrospectionResponse {
    private boolean active;
    private UUID userId;
    private String email;
    private String role;
    private List<String> authorities;
}