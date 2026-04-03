package com.furnicraft.gateway.filter;

import com.furnicraft.gateway.dto.ApiResponse;
import com.furnicraft.gateway.dto.TokenIntrospectionRequest;
import com.furnicraft.gateway.dto.TokenIntrospectionResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.core.Ordered;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Component
@RequiredArgsConstructor
public class AuthIntrospectionFilter implements GlobalFilter, Ordered {

    private static final String INTERNAL_HEADER = "X-Internal-Service-Key";

    @Qualifier("authWebClient")
    private final WebClient authWebClient;

    @org.springframework.beans.factory.annotation.Value("${application.security.internal.service-key}")
    private String internalServiceKey;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();

        if (isPublicPath(path)) {
            return chain.filter(exchange);
        }

        String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return unauthorized(exchange, "Missing or invalid Authorization header");
        }

        String token = authHeader.substring(7);

        return authWebClient.post()
                .uri("/api/v1/auth/introspect")
                .header(INTERNAL_HEADER, internalServiceKey)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new TokenIntrospectionRequest(token))
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<ApiResponse<TokenIntrospectionResponse>>() {})
                .flatMap(response -> {
                    TokenIntrospectionResponse data = response.getData();

                    if (data == null || !data.isActive()) {
                        return unauthorized(exchange, "Token is invalid or inactive");
                    }

                    ServerWebExchange mutatedExchange = exchange.mutate()
                            .request(builder -> builder
                                    .header("X-Auth-User-Id", String.valueOf(data.getUserId()))
                                    .header("X-Auth-Email", data.getEmail())
                                    .header("X-Auth-Role", data.getRole())
                                    .header("X-Auth-Authorities", String.join(",", safeAuthorities(data.getAuthorities())))
                            )
                            .build();

                    return chain.filter(mutatedExchange);
                })
                .onErrorResume(ex -> unauthorized(exchange, "Authentication service unavailable"));
    }

    private boolean isPublicPath(String path) {
        return path.startsWith("/api/v1/auth/login")
                || path.startsWith("/api/v1/auth/register")
                || path.startsWith("/api/v1/auth/refresh-token")
                || path.contains("/v3/api-docs")
                || path.startsWith("/swagger-ui")
                || path.startsWith("/webjars")
                || path.startsWith("/actuator/health")
                || path.startsWith("/actuator/info")
                || path.equals("/favicon.ico");
    }

    private List<String> safeAuthorities(List<String> authorities) {
        return authorities == null ? List.of() : authorities;
    }

    private Mono<Void> unauthorized(ServerWebExchange exchange, String message) {
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);

        String body = """
                {
                  "status": 401,
                  "message": "%s"
                }
                """.formatted(message);

        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        return exchange.getResponse().writeWith(
                Mono.just(exchange.getResponse().bufferFactory().wrap(bytes))
        );
    }

    @Override
    public int getOrder() {
        return -100;
    }
}