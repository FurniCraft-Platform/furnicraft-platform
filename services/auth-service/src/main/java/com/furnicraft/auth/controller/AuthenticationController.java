package com.furnicraft.auth.controller;

import com.furnicraft.auth.dto.*;
import com.furnicraft.auth.service.AuthenticationService;
import com.furnicraft.auth.service.TokenIntrospectionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;
    private final TokenIntrospectionService tokenIntrospectionService;

    @GetMapping("/status")
    public String getStatus() {
        return "Auth Service is UP and Running!";
    }

    @PostMapping("/register")
    public AuthenticationResponse register(@Valid @RequestBody RegisterRequest request) {
        return authenticationService.register(request);
    }

    @PostMapping("/login")
    public AuthenticationResponse login(@Valid @RequestBody LoginRequest request) {
        return authenticationService.login(request);
    }

    @PostMapping("/refresh-token")
    public AuthenticationResponse refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        return authenticationService.refreshToken(request);
    }

    @PostMapping("/logout")
    public String logout(@RequestHeader("Authorization") String authHeader) {
        authenticationService.logout(authHeader);
        return "Logged Out!";
    }

    @GetMapping("/users/{id}/exists")
    @PreAuthorize("hasRole('ADMIN') or @internalAuth.isInternalRequest()")
    public UserExistenceResponse checkUserExists(@PathVariable UUID id) {
        return authenticationService.checkUserExistsById(id);
    }

    @PostMapping("/introspect")
    @PreAuthorize("@internalAuth.isInternalRequest()")
    public ResponseEntity<TokenIntrospectionResponse> introspect(
            @Valid @RequestBody TokenIntrospectionRequest request
    ) {
        return ResponseEntity.ok(tokenIntrospectionService.introspect(request.getToken()));
    }
}
