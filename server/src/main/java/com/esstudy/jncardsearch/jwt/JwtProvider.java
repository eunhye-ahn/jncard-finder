package com.esstudy.jncardsearch.jwt;

import com.esstudy.jncardsearch.domain.Role;
import com.esstudy.jncardsearch.exception.CustomException;
import com.esstudy.jncardsearch.exception.ErrorCode;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

// jwt 토큰 생성/검증/파싱 로직 구현
// + 유저정보 꺼내기 메서드 구현
@Component
public class JwtProvider {
    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.access-expiration}")
    private Long accessExpiration;

    @Value("${jwt.refresh-expiration}")
    private Long refreshExpiration;

    //문자열 비밀키 -> jwt 서명용 키 객체로 변환
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    };

    //jwt 생성 - at
    //payload builder (subject, role, iat, exp) + 비밀키 담기
    public String generateAccessToken(Long userId, Role role) {
        return Jwts.builder()
                .subject(userId.toString())
                .claim("role",role)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + accessExpiration))
                .signWith(getSigningKey())
        .compact();
    }

    //jwt생성 -rt
    public String generateRefreshToken(Long userId) {
        return Jwts.builder()
                .subject(userId.toString())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis()+refreshExpiration))
                .signWith(getSigningKey())
                .compact();
    }

    //jwt 파싱
    private Claims pasrseClaim(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        }
        //jwtexception의 자식 오류
        catch (ExpiredJwtException e) {
            throw new CustomException(ErrorCode.EXPIRED_TOKEN);
        }
        //서명불일치, 토큰 형식 오류 등 그 외 모든 jwt 오류
        catch(JwtException e) {
            throw new CustomException(ErrorCode.INVALID_TOKEN);
        }
    };

    //jwt 검증 (파싱 결과물로 검증 진행)
    public void validateToken(String token) {
        if(token == null) throw new CustomException(ErrorCode.INVALID_TOKEN);
        pasrseClaim(token);
    }

    public Long getUserIdFromToken(String token) {
        Claims claims = pasrseClaim(token);
        return Long.parseLong(claims.getSubject());
    }

    public String getRoleFromToken(String token) {
        Claims claims = pasrseClaim(token);
        return claims.get("role", String.class);
    }

    public Long getRefreshExpiration() {
        return refreshExpiration;
    }

    public Long getRemainingExpiration(String accessToken) {
        Claims claims = pasrseClaim(accessToken);
        return claims.getExpiration().getTime()-System.currentTimeMillis();
    }
}
