package com.furnicraft.auth.controller;

import com.furnicraft.auth.dto.AuthenticationResponse;
import com.furnicraft.auth.dto.LoginRequest;
import com.furnicraft.auth.dto.RefreshTokenRequest;
import com.furnicraft.auth.dto.RegisterRequest;
import com.furnicraft.auth.service.AuthenticationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;

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
}
