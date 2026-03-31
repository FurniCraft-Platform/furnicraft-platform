package com.furnicraft.security.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Component
public class InternalHeaderAuthenticationFilter extends OncePerRequestFilter {

    private static final String HEADER_USER_ID = "X-Auth-User-Id";
    private static final String HEADER_EMAIL = "X-Auth-Email";
    private static final String HEADER_ROLE = "X-Auth-Role";
    private static final String HEADER_AUTHORITIES = "X-Auth-Authorities";

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            filterChain.doFilter(request, response);
            return;
        }

        String userId = request.getHeader(HEADER_USER_ID);
        String email = request.getHeader(HEADER_EMAIL);
        String role = request.getHeader(HEADER_ROLE);
        String authoritiesHeader = request.getHeader(HEADER_AUTHORITIES);

        if (!StringUtils.hasText(userId) || !StringUtils.hasText(email)) {
            filterChain.doFilter(request, response);
            return;
        }

        List<SimpleGrantedAuthority> authorities = parseAuthorities(authoritiesHeader);

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(email, null, authorities);

        authentication.setDetails(Map.of(
                "userId", userId,
                "email", email,
                "role", role == null ? "" : role
        ));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        filterChain.doFilter(request, response);
    }

    private List<SimpleGrantedAuthority> parseAuthorities(String authoritiesHeader) {
        if (!StringUtils.hasText(authoritiesHeader)) {
            return Collections.emptyList();
        }

        return Arrays.stream(authoritiesHeader.split(","))
                .map(String::trim)
                .filter(StringUtils::hasText)
                .map(SimpleGrantedAuthority::new)
                .toList();
    }
}