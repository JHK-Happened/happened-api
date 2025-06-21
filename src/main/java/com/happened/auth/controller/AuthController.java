package com.happened.auth.controller;

import com.happened.auth.dto.AuthResponse;
import com.happened.auth.dto.GoogleLoginRequest;
import com.happened.auth.service.GoogleAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final GoogleAuthService googleAuthService;

    @PostMapping("/google/callback")
    public ResponseEntity<?> googleLoginCallback(@RequestBody GoogleLoginRequest request) {
        try {
            AuthResponse authResponse = googleAuthService.loginOrRegister(request.getIdToken());
            return ResponseEntity.ok(authResponse);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(401).body("Error: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Server Error: " + e.getMessage());
        }
    }

}