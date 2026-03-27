package com.furnicraft.auth.client;

import com.furnicraft.auth.client.dto.UserProfileCreateRequest;
import com.furnicraft.auth.config.InternalFeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "user-service", url = "${application.clients.user-service.url}", configuration = InternalFeignConfig.class)
public interface UserServiceClient {

    @PostMapping("/api/v1/users")
    void createUserProfile(@RequestBody UserProfileCreateRequest request);
}
