package com.esstudy.jncardsearch.controller;

import com.esstudy.jncardsearch.dto.AccessTokenResponse;
import com.esstudy.jncardsearch.dto.LoginRequest;
import com.esstudy.jncardsearch.dto.TokenResponse;
import com.esstudy.jncardsearch.service.AuthService;
import com.esstudy.jncardsearch.util.CookieUtil;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;
    private final CookieUtil cookieUtil;

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request,
                                   HttpServletResponse response) {
        TokenResponse tokens = authService.login(request);

        ResponseCookie rtc = cookieUtil.createRTCookie(tokens.getRefreshToken());
        response.addHeader("Set-Cookie", rtc.toString());

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new AccessTokenResponse(tokens.getAccessToken()));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String bearer,
                                    HttpServletResponse response) {
        String accessToken = bearer.substring(7);
         authService.logout(accessToken);

         ResponseCookie rtc = cookieUtil.deleteRTCookie();
         response.addHeader("Set-Cookie", rtc.toString());

         return ResponseEntity
                 .status(HttpStatus.OK)
                 .build();
    }

    //RTR방식 (at+rt재발급) - at만료 시 사용
    @PostMapping("/reissue")
    public ResponseEntity<AccessTokenResponse> reissue(@CookieValue(name="refreshToken", required = false) String refreshToken,
                                                 HttpServletResponse response ) {
        //at+rt재발급
        TokenResponse tokens = authService.reissue(refreshToken);

        //rt는 쿠키로
        ResponseCookie rtc = cookieUtil.createRTCookie(tokens.getRefreshToken());
        response.addHeader("Set-Cookie", rtc.toString());

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new AccessTokenResponse(tokens.getAccessToken()));
    }
}
