package com.esstudy.jncardsearch.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Parameter;

// @ModelAttribute는 required 개념 없음
// 값 없으면 그냥 null or 기본값으로 처리
// setter없으면 url값 주입 못함ㄴ

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StoreSearchRequest {
    private String q;
    private String sido;
    private String category;
    private String bank;
    private String cursor;
    private int size = 10;
}
