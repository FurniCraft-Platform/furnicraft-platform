package com.furnicraft.auth.client;

import com.furnicraft.user.dto.UserCreateRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "user-service", url = "${application.clients.user-service.url}")
public interface UserServiceClient {

    @PostMapping("/api/v1/users")
    void createUserProfile(@RequestBody UserCreateRequest request);
}
