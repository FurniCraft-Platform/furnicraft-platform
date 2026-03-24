package com.furnicraft.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddressResponse {

    private UUID id;
    private String title;
    private String country;
    private String city;
    private String street;
    private String zipCode;
    private boolean isDefault;

}
