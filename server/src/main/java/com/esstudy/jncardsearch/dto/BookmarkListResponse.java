package com.esstudy.jncardsearch.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class BookmarkListResponse {
    private Long bookmarkId;
    private String storeName;
    private String category;
    private String address;
}
