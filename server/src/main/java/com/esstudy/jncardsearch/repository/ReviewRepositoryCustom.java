package com.esstudy.jncardsearch.repository;

import com.esstudy.jncardsearch.domain.Review;
import com.esstudy.jncardsearch.domain.Store;

import java.time.LocalDateTime;
import java.util.List;

/**
 * [query dsl + cursor]
 *
 * [WHAT]
 * 마지막으로 본 데이터 기준으로 다음 데이터를 가져오는 방식
 *
 * [WHY]
 * - 리뷰처럼 무한 스크롤이 필요할 때
 * - 데이터가 많아도 성능이 일정하게 유지되어야할 때
 * - offset처럼 앞 데이터를 스캔하지 않아도 되어서 빠름
 *
 * [흐름]
 * 1. 정렬용 cursorCreatedAt, cursorRating
 *      부가적 cursorId로 어디까지 봤는지 기준점 설정
 *      - 처음 요청이면 cursorId = null → 처음부터 가져옴
 * 2. sort로 정렬방식 결정 (최신순/오래된순/별점순)
 * 3. 기준점 이후 데이터를 size만큼 가져옴
 * 4. 프론트는 마지막 리뷰의 id+정렬기준 칼럼을 다음 요청의 커서로 사용
 *      - 최신순/오래된순 → id + createdAt
 *      - 별점순 → id + rating
 *
 * [단점]
 * 특정 페이지로 바로 이동 불가 (1페이지 -> 5페이지)
 * totalCount 구하기 번거로움
 */

public interface ReviewRepositoryCustom {
    List<Review> findAllByStoreWithCursor(
            Store store,
            Float minRating,
            String sort,
            Long cursorId,
            LocalDateTime cursorCreatedAt,
            Float cursorRating,
            int size
    );
}
