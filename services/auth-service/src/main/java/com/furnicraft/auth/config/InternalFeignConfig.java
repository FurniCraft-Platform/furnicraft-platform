package com.furnicraft.auth.config;

import feign.RequestInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class InternalFeignConfig {

    private static final String INTERNAL_SERVICE_KEY_HEADER = "X-Internal-Service-Key";

    @Bean
    public RequestInterceptor internalServiceRequestInterceptor(
            @Value("${application.security.internal.service-key}") String internalServiceKey
    ) {
        return requestTemplate ->
                requestTemplate.header(INTERNAL_SERVICE_KEY_HEADER, internalServiceKey);
    }
}