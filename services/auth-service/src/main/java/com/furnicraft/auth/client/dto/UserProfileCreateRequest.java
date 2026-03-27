package com.furnicraft.auth.client.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileCreateRequest {
    private UUID id;
    private String email;
    private String firstName;
    private String lastName;
}