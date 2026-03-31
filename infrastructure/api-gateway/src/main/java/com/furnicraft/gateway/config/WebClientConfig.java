package com.furnicraft.gateway.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Bean
    public WebClient authWebClient(
            WebClient.Builder builder,
            @Value("${application.services.auth.url}") String authServiceUrl
    ) {
        return builder
                .baseUrl(authServiceUrl)
                .build();
    }
}