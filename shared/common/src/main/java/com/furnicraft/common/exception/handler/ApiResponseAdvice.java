package com.furnicraft.common.exception.handler;

import com.furnicraft.common.api.ApiResponse;
import org.slf4j.MDC;
import org.springframework.core.MethodParameter;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.ResourceHttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.time.Instant;

import static com.furnicraft.common.filter.CorrelationIdFilter.CORRELATION_ID;

@RestControllerAdvice
public class ApiResponseAdvice implements ResponseBodyAdvice<Object> {

    @Override
    public boolean supports(MethodParameter returnType,
                            Class<? extends HttpMessageConverter<?>> converterType) {

        return !ApiResponse.class.isAssignableFrom(returnType.getParameterType())
                && !String.class.isAssignableFrom(returnType.getParameterType())
                && !ByteArrayHttpMessageConverter.class.isAssignableFrom(converterType)
                && !ResourceHttpMessageConverter.class.isAssignableFrom(converterType);
    }

    @Override
    public Object beforeBodyWrite(Object body,
                                  MethodParameter returnType,
                                  MediaType selectedContentType,
                                  Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                  ServerHttpRequest request,
                                  ServerHttpResponse response) {

        int status = HttpStatus.OK.value();
        if (response instanceof ServletServerHttpResponse servletResponse) {
            status = servletResponse.getServletResponse().getStatus();
        }

        if (body instanceof ApiResponse<?>) {
            return body;
        }

        if (body instanceof byte[] || body instanceof Resource) {
            return body;
        }

        return ApiResponse.builder()
                .status(status)
                .message("Success")
                .data(body)
                .timestamp(Instant.now())
                .correlationId(MDC.get(CORRELATION_ID))
                .build();
    }
}