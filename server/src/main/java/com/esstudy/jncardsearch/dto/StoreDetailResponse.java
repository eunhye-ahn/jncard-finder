package com.esstudy.jncardsearch.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class StoreDetailResponse {
    private Long storeId;
    private String storeName;
    private String sido;
    private String address;
    private String category;
    private String bank;
    private Float avgRating;
    private Integer reviewCount;
    private Integer bookmarkCount;
    private boolean isBookmarked;
}
