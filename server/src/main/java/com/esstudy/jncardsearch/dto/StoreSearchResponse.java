package com.esstudy.jncardsearch.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class StoreSearchResponse {
    private List<StoreDto> stores;
    private String nextCursor;
    private boolean hasNext;

    @Getter
    @Builder
    public static class StoreDto{
        private String id;
        private String storeName;
        private String sido;
        private String address;
    }
}