package com.furnicraft.auth.service;

import com.furnicraft.auth.client.UserServiceClient;
import com.furnicraft.auth.client.dto.UserProfileCreateRequest;
import com.furnicraft.auth.dto.*;
import com.furnicraft.auth.entity.User;
import com.furnicraft.auth.entity.enums.Role;
import com.furnicraft.auth.entity.enums.Status;
import com.furnicraft.auth.repository.UserRepository;
import com.furnicraft.common.exception.BaseException;
import com.furnicraft.common.exception.ErrorCode;
import feign.FeignException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Date;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final RedisTemplate<String, Object> redisTemplate;
    private final UserServiceClient userServiceClient;

    @Transactional
    public AuthenticationResponse register(RegisterRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new BaseException("This email is already taken!", ErrorCode.USER_ALREADY_EXISTS);
        }

        var user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .status(Status.ACTIVE)
                .build();

        userRepository.save(user);

        try {
            userServiceClient.createUserProfile(UserProfileCreateRequest.builder()
                    .id(user.getId())
                    .email(user.getEmail())
                    .firstName(user.getFirstName())
                    .lastName(user.getLastName())
                    .build());
        } catch (FeignException ex) {
            userRepository.delete(user);
            throw new RuntimeException(
                    "User profile creation failed. Status: " + ex.status() + ", body: " + ex.contentUTF8(),
                    ex
            );
        } catch (Exception ex) {
            userRepository.delete(user);
            throw new RuntimeException("User profile creation failed. Real cause: " + ex.getMessage(), ex);
        }

        var jwtToken = jwtService.generateToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);

        return AuthenticationResponse.builder()
                .accessToken(jwtToken)
                .refreshToken(refreshToken)
                .build();
    }

    public AuthenticationResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        var user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        var jwtToken = jwtService.generateToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);

        return AuthenticationResponse.builder()
                .accessToken(jwtToken)
                .refreshToken(refreshToken)
                .build();
    }

    public void logout(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return;
        }

        String jwt = authHeader.substring(7);

        if (!jwtService.isAccessToken(jwt)) {
            return;
        }

        Date expirationDate = jwtService.extractExpiration(jwt);
        long diff = expirationDate.getTime() - System.currentTimeMillis();

        if (diff > 0) {
            redisTemplate.opsForValue().set(jwt, "blacklisted", Duration.ofMillis(diff));
        }
    }

    public AuthenticationResponse refreshToken(RefreshTokenRequest request) {
        String refreshToken = request.getRefreshToken();

        String userEmail = jwtService.extractUsername(refreshToken);

        if (userEmail != null) {
            var user = userRepository.findByEmail(userEmail)
                    .orElseThrow(() -> new BaseException("User not found", ErrorCode.RESOURCE_NOT_FOUND));

            if (jwtService.isTokenValid(refreshToken, user)) {
                var accessToken = jwtService.generateToken(user);

                return AuthenticationResponse.builder()
                        .accessToken(accessToken)
                        .refreshToken(refreshToken)
                        .build();
            }
        }

        throw new BaseException("Refresh token is invalid or expired", ErrorCode.TOKEN_EXPIRED);
    }

    public UserExistenceResponse checkUserExistsById(UUID userId) {
        boolean exists = userRepository.existsById(userId);
        return UserExistenceResponse.builder()
                .exists(exists)
                .build();
    }
}
