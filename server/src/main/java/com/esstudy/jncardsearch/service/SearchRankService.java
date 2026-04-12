package com.esstudy.jncardsearch.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 ES score    →  요청할 때마다 실시간 계산
 "악기점" 검색 → 문서들 관련도 계산 → 응답
 → 저장 안 함, 다음 요청에 또 새로 계산

 Redis score →  검색할 때마다 누적
 "악기점" 1번 검색 → 1점
 "악기점" 2번 검색 → 2점
 → Redis가 살아있는 한 계속 쌓임

=======================================보충
 서버 꺼지면 → 데이터 날아감 (기본값)

 해결하려면
 → Redis 영속성 설정 (AOF, RDB)
 → docker-compose에 옵션 추가
 */

@Service
@RequiredArgsConstructor
public class SearchRankService {
    private final RedisTemplate redisTemplate;

    //redis에서 사용할 키 이름 (sorted set)
    //search:rank = {"악기점": 5, "카페": 10}
    private static final String RANK_KEY = "search:rank";

    /**
     * [WHAT] 검색어 점수 +1 메서드
     * [WHY] 검색할때마다 카운트해서 인기검색어 집계
     * [흐름]
     * 검색어입력 -> incrementScore("카페")
     * -> redis sorted set에서 점수+1
     * -> 없으면 새로 생성 후 1점, 있으면 기존점수에 +1
     */
    public void incrementScore(String q){
        if(!StringUtils.hasText(q)) return;
        redisTemplate.opsForZSet().incrementScore(RANK_KEY, q, 1.0);
    }

    /**
     * [WHAT] 인기검색어 Top 10 조회
     * [WHY] 점수 높은 순으로 상위 10개 반환
     * [흐름]
     * reverseRange(KEY, 0, 9)
     * -> sortedset에서 점수 높은순으로 (0~9) 10개 조회
     * -> null이면 빈리스트 반환 (NPE 방지) <- List.of()
     */
    public List<String> getTopKeywords(){
        Set<String> result = redisTemplate.opsForZSet()
                .reverseRange(RANK_KEY, 0, 9);
        return result != null ? new ArrayList<>(result) : List.of();
    }
}
