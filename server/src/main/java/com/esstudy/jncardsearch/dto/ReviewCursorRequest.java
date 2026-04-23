package com.esstudy.jncardsearch.dto;

import lombok.*;

import java.time.LocalDateTime;

/**
 * @ModelAtrribute : NoArgs로 객체 만들고 setter로 값 주입
 */

@Getter
@NoArgsConstructor
@Setter
@AllArgsConstructor
@Builder
public class ReviewCursorRequest {
    private Float minRating;
    private String sort;
    private Long cursorId;
    private LocalDateTime cursorCreatedAt;
    private Float cursorRating;
}
