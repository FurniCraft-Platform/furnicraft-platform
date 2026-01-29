package com.furnicraft.auth.controller;

import com.furnicraft.auth.dto.AuthenticationResponse;
import com.furnicraft.auth.dto.LoginRequest;
import com.furnicraft.auth.dto.RegisterRequest;
import com.furnicraft.auth.service.AuthenticationService;
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
    public ResponseEntity<AuthenticationResponse> register(@RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authenticationService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(authenticationService.login(request));
    }

    @PostMapping("/logout")
    public String logout(@RequestHeader("Authorization") String authHeader) {
        authenticationService.logout(authHeader);
        return "Logged Out!";
    }
}
