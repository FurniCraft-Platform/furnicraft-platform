package com.furnicraft.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class TokenIntrospectionRequest {

    @NotBlank(message = "Token must not be blank")
    private String token;
}