package com.esstudy.jncardsearch.controller;

import com.esstudy.jncardsearch.dto.LoginRequest;
import com.esstudy.jncardsearch.dto.TokenResponse;
import com.esstudy.jncardsearch.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        TokenResponse token = authService.login(request);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(token);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String bearer) {
        String accessToken = bearer.substring(7);
         authService.logout(accessToken);
         return ResponseEntity
                 .status(HttpStatus.OK)
                 .build();
    }
}
