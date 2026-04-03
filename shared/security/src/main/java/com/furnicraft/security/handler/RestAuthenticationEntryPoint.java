package com.furnicraft.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.furnicraft.common.dto.ApiResponse;
import com.furnicraft.common.exception.ErrorCode;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Instant;

import static com.furnicraft.common.filter.CorrelationIdFilter.CORRELATION_ID;

@Component
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    public RestAuthenticationEntryPoint(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException
    ) throws IOException, ServletException {

        ApiResponse<?> apiResponse = ApiResponse.builder()
                .status(ErrorCode.UNAUTHORIZED.getStatus().value())
                .message(ErrorCode.UNAUTHORIZED.getDefaultMessage())
                .timestamp(Instant.now())
                .correlationId(MDC.get(CORRELATION_ID))
                .errors(authException.getMessage())
                .build();

        response.setStatus(ErrorCode.UNAUTHORIZED.getStatus().value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        objectMapper.writeValue(response.getOutputStream(), apiResponse);
    }
}