package com.esstudy.jncardsearch.service;

import com.esstudy.jncardsearch.domain.Review;
import com.esstudy.jncardsearch.domain.Store;
import com.esstudy.jncardsearch.domain.User;
import com.esstudy.jncardsearch.dto.MyReviewResponse;
import com.esstudy.jncardsearch.dto.ReviewCursorRequest;
import com.esstudy.jncardsearch.dto.ReviewCursorResponse;
import com.esstudy.jncardsearch.dto.ReviewRequest;
import com.esstudy.jncardsearch.dto.StoreReviewResponse;
import com.esstudy.jncardsearch.exception.CustomException;
import com.esstudy.jncardsearch.exception.ErrorCode;
import com.esstudy.jncardsearch.repository.ReviewRepository;
import com.esstudy.jncardsearch.repository.StoreRepository;
import com.esstudy.jncardsearch.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {
    private final UserRepository userRepository;
    private final StoreRepository storeRepository;
    private final ReviewRepository reviewRepository;
    private final StoreService storeService;

    private static final int REVIEW_PAGE_SIZE = 10;

    //리뷰작성
    public MyReviewResponse addReview(ReviewRequest request, Long userId, Long storeId) {

        //유저정보
        User user = userRepository.findById(userId).orElseThrow(
                () -> new CustomException(ErrorCode.USER_NOT_FOUND)
        );
        //스토어정보
        Store store = storeRepository.findById(storeId).orElseThrow(
                () -> new CustomException(ErrorCode.STORE_NOT_FOUND)
        );

        //중복리뷰 체크
        if(reviewRepository.existsByUserAndStore(user, store)){
            throw new CustomException(ErrorCode.DUPLICATE_REVIEW);
        }

        //리뷰 db 저장
        Review review = reviewRepository.save(Review.builder()
                        .user(user)
                        .store(store)
                        .content(request.getContent())
                        .rating(request.getRating())
                .build());

        storeService.incrementReviewCount(String.valueOf(storeId), +1);

        return MyReviewResponse.builder()
                .reviewId(review.getId())
                .storeName(review.getStore().getStoreName())
                .content(review.getContent())
                .rating(review.getRating())
                .reviewDate(review.getCreatedAt().toLocalDate())
                .build();
    }

    //review 삭제
    public void deleteReview(Long reviewId, Long userId, Long storeId) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new CustomException(ErrorCode.USER_NOT_FOUND)
        );

        Review review = reviewRepository.findById(reviewId).orElseThrow(
                ()-> new CustomException(ErrorCode.REVIEW_NOT_FOUND)
        );

        //본인 리뷰맞는지 확인
        if(!review.getUser().getId().equals(userId)){
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }

        //리뷰삭제
        reviewRepository.deleteById(reviewId);

        storeService.incrementReviewCount(String.valueOf(storeId), -1);
    }

    //특정 가맹점의 review 조회
    public ReviewCursorResponse getReviewsByStore(Long storeId, ReviewCursorRequest request){
        Store store = storeRepository.findById(storeId).orElseThrow(
                () -> new CustomException(ErrorCode.STORE_NOT_FOUND)
        );

        //스트림 정렬 : db에서 데이터를 전부 다 가져온 다음 메모리에서 정렬
        // vs db정렬 : db안에서 정렬 후 정렬된 결과만 java로 로드 <- 인덱스활용으로 최적화도 가능
        List<StoreReviewResponse> reviews = reviewRepository.findAllByStoreWithCursor(store,
                        request.getMinRating(),
                        request.getSort(),
                        request.getCursorId(),
                        request.getCursorCreatedAt(),
                        request.getCursorRating(),
                        REVIEW_PAGE_SIZE)
                .stream()
                .map(review -> StoreReviewResponse.builder()
                        .reviewId(review.getId())
                        .content(review.getContent())
                        .rating(review.getRating())
                        .reviewerName(review.getUser().getName())
                        .reviewDate(review.getCreatedAt())
                        .build())
                .toList();

        //마지막 리뷰가 다음 커서
        StoreReviewResponse last = reviews.isEmpty() ? null : reviews.get(reviews.size() - 1);

        return ReviewCursorResponse.builder()
                .reviews(reviews)
                .nextCursorId(last != null ? last.getReviewId() : null)
                .nextCursorCreatedAt(last != null ? last.getReviewDate() : null)
                .nextCursorRating(last != null ? last.getRating() : null)
                .hasNext(reviews.size() == REVIEW_PAGE_SIZE)
                .build();
    }

    //내 review 조회
    public List<MyReviewResponse> getReviewsByUser(Long userId){
        User user = userRepository.findById(userId).orElseThrow(
                () -> new CustomException(ErrorCode.USER_NOT_FOUND)
        );

        return reviewRepository.findAllByUser(user).stream()
                .map(review -> MyReviewResponse.builder()
                        .reviewId(review.getId())
                        .content(review.getContent())
                        .rating(review.getRating())
                        .storeName(review.getStore().getStoreName())
                        .reviewDate(review.getCreatedAt().toLocalDate())
                        .build()
                )
                .toList();
    }

}
