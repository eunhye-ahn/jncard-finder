package com.esstudy.jncardsearch.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Parameter;

// @ModelAttribute는 required 개념 없음
// 값 없으면 그냥 null or 기본값으로 처리

@Getter
public class StoreSearchRequest {
    @Schema(description="검색어", example = "피자")
    private String q;

    @Schema(description="시도 필터", example = "서울")
    private String sido;

    //커서페이징
    @Schema(description = "커서값, 첫요청은 null")
    private String cursor;

    //무한스크롤이랑 같이
    @Schema(description = "페이지크기", defaultValue = "10", example = "10")
    private int size = 10;
}
