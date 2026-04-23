package com.esstudy.jncardsearch.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class ReviewCursorResponse {
    private List<StoreReviewResponse> reviews;
    private Long nextCursorId;
    private LocalDateTime nextCursorCreatedAt;
    private Float nextCursorRating;
    private boolean hasNext;
}
