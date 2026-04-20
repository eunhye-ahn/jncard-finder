package com.esstudy.jncardsearch.repository;

import com.esstudy.jncardsearch.domain.Bookmark;
import com.esstudy.jncardsearch.domain.Store;
import com.esstudy.jncardsearch.domain.User;
import com.esstudy.jncardsearch.dto.BookmarkCountDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {
    boolean existsByUserAndStore(User user, Store store);

    void deleteByUserAndStore(User user, Store store);

    /**
     * [WHAT] Scheduler에 전달할 DB 상태 조회
     * [WHY] JPA 메서드 네이밍은 find, exists, delete 같은 단순 조회/삭제만 지원
     *          집계 연산은 @Query로 직접 작성
     * [흐름]
     * BookmarkCountDto : JPA Projection
     *      -> 쿼리 결과 자동 매칭
     *
     * [Projection vs 클래스 dto]
     * Projection - 쿼리 결과 몇개 필드만 뽑을 때 간편
     *                  -> DB ↔ 서비스 사이에서만 사용
     * 클래스 dto - json 직렬화, 빌더 패턴, 검증 등 다양하게 활용
     */
    @Query("select b.store.id as storeId, count(b) as count from Bookmark b group by b.store.id")
    List<BookmarkCountDto> countGroupByStoreId();

    //n+1 방지 -> 조인패치
    @Query("select b from Bookmark b join fetch b.store where b.user = :user")
    List<Bookmark> findByUserWithStore(@Param("user") User user);
}
