package com.furnicraft.common.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.UUID;

@Component
public class CorrelationIdFilter implements Filter {

    public static final String CORRELATION_ID = "X-Correlation-Id";

    @Override
    public void doFilter(ServletRequest request,
                         ServletResponse response,
                         FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;

        String correString =
                httpRequest.getHeader(CORRELATION_ID) != null
                        ? httpRequest.getHeader(CORRELATION_ID)
                        : UUID.randomUUID().toString();

        MDC.put(CORRELATION_ID, correString);

        try {
            chain.doFilter(request, response);
        } finally {
            MDC.clear();
        }
    }
}
