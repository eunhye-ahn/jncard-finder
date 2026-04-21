package com.esstudy.jncardsearch.controller;

import com.esstudy.jncardsearch.domain.Review;
import com.esstudy.jncardsearch.dto.MyReviewResponse;
import com.esstudy.jncardsearch.dto.ReviewRequest;
import com.esstudy.jncardsearch.dto.StoreReviewResponse;
import com.esstudy.jncardsearch.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService reviewService;

    @PostMapping("/{storeId}")
    public ResponseEntity<MyReviewResponse> createReview(@AuthenticationPrincipal Long userId,
                                 @PathVariable Long storeId,
                                 @RequestBody ReviewRequest request) {
        //리뷰저장
        MyReviewResponse result = reviewService.addReview(request, userId, storeId);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(result);
    }

    @DeleteMapping("/{reviewId}")
    public ResponseEntity<Void> deleteReview(@AuthenticationPrincipal Long userId,
                                                         @PathVariable Long reviewId, @RequestParam Long storeId) {
        reviewService.deleteReview(reviewId, userId, storeId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .build();
    }

    @GetMapping("/{storeId}")
    public ResponseEntity<List<StoreReviewResponse>> getStoreReviews(@PathVariable Long storeId) {
        List<StoreReviewResponse> result = reviewService.getReviewsByStore(storeId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(result);
    }

    @GetMapping("/my")
    public ResponseEntity<List<MyReviewResponse>> getMyReviews(@AuthenticationPrincipal Long userId) {
        List<MyReviewResponse> result = reviewService.getReviewsByUser(userId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(result);
    }
}
