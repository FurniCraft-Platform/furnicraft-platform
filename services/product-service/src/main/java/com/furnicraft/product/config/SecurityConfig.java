package com.furnicraft.product.config;

import com.furnicraft.security.config.SecurityConfigSupport;
import com.furnicraft.security.filter.InternalHeaderAuthenticationFilter;
import com.furnicraft.security.handler.RestAccessDeniedHandler;
import com.furnicraft.security.handler.RestAuthenticationEntryPoint;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity
public class SecurityConfig extends SecurityConfigSupport {

    public SecurityConfig(
            InternalHeaderAuthenticationFilter internalHeaderAuthenticationFilter,
            RestAuthenticationEntryPoint restAuthenticationEntryPoint,
            RestAccessDeniedHandler restAccessDeniedHandler
    ) {
        super(internalHeaderAuthenticationFilter, restAuthenticationEntryPoint, restAccessDeniedHandler);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/v3/api-docs/**",
                                "/api/v1/products/v3/api-docs/**",
                                "/actuator/health",
                                "/actuator/health/**",
                                "/actuator/info"
                        ).permitAll()
                        .anyRequest().authenticated()
                );

        return build(http);
    }
}