package com.furnicraft.user.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "auth-service", url = "${application.clients.auth-service.url}")
public interface AuthServiceClient {

    @GetMapping("/api/v1/auth/users/{id}/exists")
    boolean checkUserExists(@PathVariable("id") UUID id);
}
