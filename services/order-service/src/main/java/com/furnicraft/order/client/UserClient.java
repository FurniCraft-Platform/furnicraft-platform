package com.furnicraft.order.client;

import com.furnicraft.order.client.response.AddressResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "user-service", path = "/api/v1/users")
public interface UserClient {
    @GetMapping("/{userId}/addresses/{addressId}")
    AddressResponse getAddressById(
            @PathVariable UUID userId,
            @PathVariable UUID addressId
    );
}