package com.furnicraft.auth.controller;

import com.furnicraft.common.exception.BaseException;
import com.furnicraft.common.exception.ErrorCode;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    @GetMapping("/status")
    public String getStatus() {
        return "Auth Service is UP and Running!";
    }

    @GetMapping("/success")
    public Map<String, String> testSuccess() {
        return Map.of("result", "Everything is fine!");
    }

    @GetMapping("/error")
    public void testError() {
        throw new BaseException("Test xətası!", ErrorCode.INTERNAL_ERROR);
    }
}
