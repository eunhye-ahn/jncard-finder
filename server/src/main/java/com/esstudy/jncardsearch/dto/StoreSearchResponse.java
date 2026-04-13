package com.esstudy.jncardsearch.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

/**
 * 검색응답에 쓰는 dto인게 확실할때
 * 내부클래스에 묶는게 더 가독성좋음.
 */

@Getter
@Builder
public class StoreSearchResponse {
    private List<StoreDto> stores;
    private String nextCursor;
    private boolean hasNext;

    @Getter
    @Builder
    public static class StoreDto{
        private Long storeId;
        private String storeName;
        private String sido;
        private String address;
        private String bank;
    }
}