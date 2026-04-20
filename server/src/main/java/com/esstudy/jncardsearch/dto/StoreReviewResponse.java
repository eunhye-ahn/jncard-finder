package com.esstudy.jncardsearch.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
@Builder
public class StoreReviewResponse {
    private Long reviewId;
    private String content;
    private Float rating;
    private LocalDate reviewDate;
    private String reviewerName;
}
