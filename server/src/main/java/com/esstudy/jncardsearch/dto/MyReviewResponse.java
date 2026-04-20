package com.esstudy.jncardsearch.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
@AllArgsConstructor
public class MyReviewResponse {
    private Long reviewId;
    private String content;
    private Float rating;
    private LocalDate reviewDate;
    private String storeName;
}
