package com.furnicraft.security.feign;

import feign.RequestInterceptor;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Configuration
public class FeignAuthRelayConfig {

    private static final String AUTHORIZATION = "Authorization";
    private static final String INTERNAL_SERVICE_KEY = "X-Internal-Service-Key";

    @Bean
    public RequestInterceptor authRelayRequestInterceptor() {
        return requestTemplate -> {
            RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();

            if (!(requestAttributes instanceof ServletRequestAttributes servletRequestAttributes)) {
                return;
            }

            HttpServletRequest request = servletRequestAttributes.getRequest();

            String authorizationHeader = request.getHeader(AUTHORIZATION);
            if (authorizationHeader != null && !authorizationHeader.isBlank()) {
                requestTemplate.header(AUTHORIZATION, authorizationHeader);
            }

            String internalServiceKey = request.getHeader(INTERNAL_SERVICE_KEY);
            if (internalServiceKey != null && !internalServiceKey.isBlank()) {
                requestTemplate.header(INTERNAL_SERVICE_KEY, internalServiceKey);
            }
        };
    }
}