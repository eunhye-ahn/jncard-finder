package com.esstudy.jncardsearch.controller;

import com.esstudy.jncardsearch.dto.SignUpRequest;
import com.esstudy.jncardsearch.dto.TokenResponse;
import com.esstudy.jncardsearch.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/api/auth/signup")
    public ResponseEntity<TokenResponse> signup(@Valid @RequestBody SignUpRequest request) {
        TokenResponse token = userService.save(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(token);
    }
}
