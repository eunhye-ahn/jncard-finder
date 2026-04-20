package com.esstudy.jncardsearch.service;

import com.esstudy.jncardsearch.domain.Review;
import com.esstudy.jncardsearch.domain.Store;
import com.esstudy.jncardsearch.domain.User;
import com.esstudy.jncardsearch.dto.MyReviewResponse;
import com.esstudy.jncardsearch.dto.ReviewRequest;
import com.esstudy.jncardsearch.dto.StoreReviewResponse;
import com.esstudy.jncardsearch.exception.CustomException;
import com.esstudy.jncardsearch.exception.ErrorCode;
import com.esstudy.jncardsearch.repository.ReviewRepository;
import com.esstudy.jncardsearch.repository.StoreRepository;
import com.esstudy.jncardsearch.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {
    private final UserRepository userRepository;
    private final StoreRepository storeRepository;
    private final ReviewRepository reviewRepository;

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

        return MyReviewResponse.builder()
                .reviewId(review.getId())
                .storeName(review.getStore().getStoreName())
                .content(review.getContent())
                .rating(review.getRating())
                .reviewDate(review.getCreatedAt().toLocalDate())
                .build();
    }

    //review 삭제
    public void deleteReview(Long reviewId, Long userId){
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
    }

    //특정 가맹점의 review 조회
    public List<StoreReviewResponse> getReviewsByStore(Long storeId){
        Store store = storeRepository.findById(storeId).orElseThrow(
                () -> new CustomException(ErrorCode.STORE_NOT_FOUND)
        );

        return reviewRepository.findAllByStore(store).stream()
                .map(review -> StoreReviewResponse.builder()
                        .reviewId(review.getId())
                        .content(review.getContent())
                        .rating(review.getRating())
                        .reviewerName(review.getUser().getName())
                        .reviewDate(review.getCreatedAt().toLocalDate())
                        .build())
                .toList();
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
