package com.esstudy.jncardsearch.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor //modelattribute는 기본생성자 필요
@Builder
public class ReviewCursorRequest {
    private Float minRating;
    private String sort;
    private Long cursorId;
    private LocalDateTime cursorCreatedAt;
    private Float cursorRating;
}
