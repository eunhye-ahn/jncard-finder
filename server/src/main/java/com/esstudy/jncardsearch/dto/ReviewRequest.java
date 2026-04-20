package com.esstudy.jncardsearch.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ReviewRequest {
    private String content;
    private Float rating;
}