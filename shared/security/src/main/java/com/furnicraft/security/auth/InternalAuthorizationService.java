package com.furnicraft.security.auth;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class InternalAuthorizationService {

    private static final String INTERNAL_HEADER = "X-Internal-Service-Key";

    private final HttpServletRequest request;
    private final String internalServiceKey;

    public boolean isInternalRequest() {
        String headerValue = request.getHeader(INTERNAL_HEADER);

        return headerValue != null
                && !headerValue.isBlank()
                && headerValue.equals(internalServiceKey);
    }
}