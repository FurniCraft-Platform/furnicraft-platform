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

    private static final String AUTH_USER_ID = "X-Auth-User-Id";
    private static final String AUTH_EMAIL = "X-Auth-Email";
    private static final String AUTH_ROLE = "X-Auth-Role";
    private static final String AUTH_AUTHORITIES = "X-Auth-Authorities";

    @Bean
    public RequestInterceptor authRelayRequestInterceptor() {
        return requestTemplate -> {
            RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();

            if (!(requestAttributes instanceof ServletRequestAttributes servletRequestAttributes)) {
                return;
            }

            HttpServletRequest request = servletRequestAttributes.getRequest();

            relayHeader(request, requestTemplate, AUTHORIZATION);
            relayHeader(request, requestTemplate, INTERNAL_SERVICE_KEY);

            relayHeader(request, requestTemplate, AUTH_USER_ID);
            relayHeader(request, requestTemplate, AUTH_EMAIL);
            relayHeader(request, requestTemplate, AUTH_ROLE);
            relayHeader(request, requestTemplate, AUTH_AUTHORITIES);
        };
    }

    private void relayHeader(HttpServletRequest request, feign.RequestTemplate requestTemplate, String headerName) {
        String value = request.getHeader(headerName);
        if (value != null && !value.isBlank()) {
            requestTemplate.header(headerName, value);
        }
    }
}