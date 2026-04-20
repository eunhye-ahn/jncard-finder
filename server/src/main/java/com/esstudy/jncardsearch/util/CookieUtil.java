package com.esstudy.jncardsearch.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

@Component
public class CookieUtil {
    @Value("${jwt.refresh-expiration}")
    private Long refreshExp;

    /**
     * // CookieUtil → ResponseCookie 생성
     * ResponseCookie cookie = cookieUtil.createRTCookie(rtValue)
     */
    public ResponseCookie createRTCookie(String refreshToken) {
        return ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .path("/")
                .maxAge(refreshExp)
                .sameSite("Lax")
                .secure(false)
                .build();
    }

    /**
     * 쿠키 삭제
     */
    public ResponseCookie deleteRTCookie(){
        return ResponseCookie.from("refreshToken","")
                .httpOnly(true)
                .path("/")
                .maxAge(0)  //브라우저가 쿠키 즉시삭제
                .sameSite("Lax")
                .secure(false)
                .build();
    }
}
