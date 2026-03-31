package com.furnicraft.auth.service;

import com.furnicraft.auth.dto.TokenIntrospectionResponse;
import com.furnicraft.auth.entity.User;
import com.furnicraft.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TokenIntrospectionService {

    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final TokenBlacklistService tokenBlacklistService;

    public TokenIntrospectionResponse introspect(String token) {
        try {
            if (token == null || token.isBlank()) {
                return inactive();
            }

            if (!jwtService.isAccessToken(token)) {
                return inactive();
            }

            if (tokenBlacklistService.isBlacklisted(token)) {
                return inactive();
            }

            String email = jwtService.extractUsername(token);

            User user = userRepository.findByEmail(email).orElse(null);
            if (user == null) {
                return inactive();
            }

            if (!jwtService.isTokenValid(token, user)) {
                return inactive();
            }

            List<String> authorities = user.getAuthorities()
                    .stream()
                    .map(GrantedAuthority::getAuthority)
                    .toList();

            return TokenIntrospectionResponse.builder()
                    .active(true)
                    .userId(user.getId())
                    .email(user.getEmail())
                    .role(user.getRole().name())
                    .authorities(authorities)
                    .build();

        } catch (Exception ex) {
            return inactive();
        }
    }

    private TokenIntrospectionResponse inactive() {
        return TokenIntrospectionResponse.builder()
                .active(false)
                .build();
    }
}