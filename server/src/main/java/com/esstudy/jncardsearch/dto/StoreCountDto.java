package com.esstudy.jncardsearch.dto;

public interface StoreCountDto {
    String getStoreId();
    Long getBookmarkCount();
    Long getReviewCount();
    Float getAvgRating();
}