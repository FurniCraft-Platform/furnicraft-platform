package com.furnicraft.auth.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.furnicraft.security.handler.RestAccessDeniedHandler;
import com.furnicraft.security.handler.RestAuthenticationEntryPoint;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SecurityBeansConfig {

    @Bean
    public RestAuthenticationEntryPoint restAuthenticationEntryPoint(ObjectMapper objectMapper) {
        return new RestAuthenticationEntryPoint(new ObjectMapper());
    }

    @Bean
    public RestAccessDeniedHandler restAccessDeniedHandler(ObjectMapper objectMapper) {
        return new RestAccessDeniedHandler(new ObjectMapper());
    }
}
