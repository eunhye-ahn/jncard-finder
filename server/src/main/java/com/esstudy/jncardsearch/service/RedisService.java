package com.esstudy.jncardsearch.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * [흐름] 클라이언트 -> 시큐리티필터 통과
 * -> jwtAuthFilter - 토큰 검증, Redis조회() : 블랙리스트 조회, 인증정보저장
 * -> 컨트롤러
 *
 * [WHAT] redis 에 rt 저장,조회,삭제, at 블랙아웃리스트 저장,조회
 *
 * [저장형태]
 * [RT]
 * key: "RT:1" , vlaue:"eyJhbGci..."
 * [BL]
 * key: "BL:eyJhbGci...", vlaue:value: "logout"
 */

@Service
@RequiredArgsConstructor
public class RedisService {
    /**
     * [WHAT] 스프링이 제공하는 reids 연산 추상화...
     * [WHY] redis는 tcp 소켓으로 직접 통신해야하는데, 그 복잡한 연결/직렬화/명령 전송을
     *      redistemplate가 대신 처리 -> 개발자는 비즈니스로직만 작성
     *
     *      redis에 저장할때 데이터만 저장 - 영구저장 / 데이터,ttl저장 - 알아서 삭제
     */
    private final RedisTemplate<String, String> redisTemplate;

    //rt 저장
    public void saveRefreshToken(Long userId, String refreshToken, long expiration) {
        redisTemplate.opsForValue()
                .set("RT:"+userId, refreshToken, expiration, TimeUnit.MILLISECONDS);
    }

    //rt 조회
    public String getRefreshToken(Long userId) {
        return redisTemplate.opsForValue().get("RT:"+userId);
    }

    //rt 삭제
    public void deleteRefreshToken(Long userId) {
        redisTemplate.delete("RT:"+userId);
    }

    //at br등록
    public void addBlackList(String accessToken, Long remainingExpiration) {
        redisTemplate.opsForValue()
                .set("BL:"+accessToken, "logout", remainingExpiration, TimeUnit.MILLISECONDS);
    }

    //br조회
    public boolean isBlackList(String accessToken) {
        return redisTemplate.hasKey("BL:"+accessToken);
    }
}
