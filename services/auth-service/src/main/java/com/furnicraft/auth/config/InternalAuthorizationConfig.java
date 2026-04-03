package com.furnicraft.auth.config;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class InternalAuthorizationConfig {

    @Bean(name = "internalAuth")
    public InternalAuthEvaluator internalAuthEvaluator(
            HttpServletRequest request,
            @Value("${application.security.internal.service-key}") String internalServiceKey
    ) {
        return new InternalAuthEvaluator(request, internalServiceKey);
    }

    public static class InternalAuthEvaluator {

        private static final String INTERNAL_HEADER = "X-Internal-Service-Key";

        private final HttpServletRequest request;
        private final String internalServiceKey;

        public InternalAuthEvaluator(HttpServletRequest request, String internalServiceKey) {
            this.request = request;
            this.internalServiceKey = internalServiceKey;
        }

        public boolean isInternalRequest() {
            String headerValue = request.getHeader(INTERNAL_HEADER);
            return headerValue != null && headerValue.equals(internalServiceKey);
        }
    }
}