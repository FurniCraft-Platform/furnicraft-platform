package com.furnicraft.security.config;

import com.furnicraft.security.auth.InternalAuthorizationService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class InternalAuthorizationConfig {

    @Bean(name = "internalAuth")
    public InternalAuthorizationService internalAuthorizationService(HttpServletRequest request,
                                                                     @Value("${application.security.internal.service-key}") String internalServiceKey
    ) {
        return new InternalAuthorizationService(request, internalServiceKey);
    }
}