package com.furnicraft.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.furnicraft.common.dto.ApiResponse;
import com.furnicraft.common.exception.ErrorCode;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Instant;

import static com.furnicraft.common.filter.CorrelationIdFilter.CORRELATION_ID;

@Component
public class RestAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper;

    public RestAccessDeniedHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void handle(
            HttpServletRequest request,
            HttpServletResponse response,
            AccessDeniedException accessDeniedException
    ) throws IOException, ServletException {

        ApiResponse<?> apiResponse = ApiResponse.builder()
                .status(ErrorCode.ACCESS_DENIED.getStatus().value())
                .message(ErrorCode.ACCESS_DENIED.getDefaultMessage())
                .timestamp(Instant.now())
                .correlationId(MDC.get(CORRELATION_ID))
                .errors(accessDeniedException.getMessage())
                .build();

        response.setStatus(ErrorCode.ACCESS_DENIED.getStatus().value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        objectMapper.writeValue(response.getOutputStream(), apiResponse);
    }
}