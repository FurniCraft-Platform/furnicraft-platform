package com.furnicraft.security.auth;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service("internalAuth")
@RequiredArgsConstructor
public class InternalAuthorizationService {

    private static final String INTERNAL_SERVICE_KEY_HEADER = "X-Internal-Service-Key";

    private final HttpServletRequest request;

    @Value("${application.security.internal.service-key}")
    private String internalServiceKey;

    public boolean isInternalRequest() {
        String headerValue = request.getHeader(INTERNAL_SERVICE_KEY_HEADER);

        return headerValue != null
                && !headerValue.isBlank()
                && headerValue.equals(internalServiceKey);
    }
}