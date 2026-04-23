package com.esstudy.jncardsearch.repository;

import com.esstudy.jncardsearch.domain.QReview;
import com.esstudy.jncardsearch.domain.Review;
import com.esstudy.jncardsearch.domain.Store;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/*
 * [흐름] Q클래스() 자동생성 : entity의 필드들을 참조할 수 있게 하는 클래스
 *          -> BooleanBuilder 방식 : if문으로 직접 조건 추가 - and or
 *          -> BooleanExpression 방식 : null이면 자동 무시 - 커서가 있냐없냐만 일때/???
 *      -> Repository에 상속 (인터페이스로)
 *      -> Service에 서비스
 *      -> 컨트롤러에 엔드포인트 추가
 *
 *  * querydsl 비교 메서드
 * .eq 같다
 * .ne 다르다
 * .gt 크다
 * .lt 작다
 * .goe 크거나 같다
 * .loe 작거나 같다
 * .isNull
 * .contains 포함한다
*/
@Repository
@RequiredArgsConstructor
public class ReviewRepositoryImpl implements ReviewRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Review> findAllByStoreWithCursor(Store store, Float minRating, String sort, Long cursorId,
                                                 LocalDateTime cursorCreatedAt, Float cursorRating, int size) {
        QReview review = QReview.review;
        BooleanBuilder builder = new BooleanBuilder();

        //가게조건
        builder.and(review.store.eq(store));

        //별점 필터
        if(minRating != null) builder.and(review.rating.goe(minRating));

        //커서 조건 (커서 시작 위치 잡기)
        if(cursorId != null){
            switch(sort)
            {
                //오래된순
                case "oldest" -> builder.and(review.createdAt.gt(cursorCreatedAt)
                        .or(review.createdAt.eq(cursorCreatedAt).and(review.id.gt(cursorId))));
                //별점순
                case "rating" -> builder.and(review.rating.lt(cursorRating)
                                .or(review.rating.eq(cursorRating).and(review.id.lt(cursorId))));
                //최신순(기본)
                default -> builder.and(review.createdAt.lt(cursorCreatedAt)
                        .or(review.createdAt.eq(cursorCreatedAt).and(review.id.lt(cursorId))));
            }
        }

        //정렬조건
        OrderSpecifier<?> orderBy = switch(sort){
            case "latest" -> review.createdAt.desc();
            case "oldest" -> review.createdAt.asc();
            case "rating" -> review.rating.desc();
            default -> review.createdAt.desc();
        };

        OrderSpecifier<Long> idOrder = sort.equals("oldest")
                ? review.id.asc()
                : review.id.desc();

        //실제 쿼리 실행 + 정렬
        return queryFactory
                .selectFrom(review)
                .leftJoin(review.user).fetchJoin()
                .where(builder)
                .orderBy(orderBy, idOrder) //2차정렬 id
                .limit(size)
                .fetch();
    }
}
