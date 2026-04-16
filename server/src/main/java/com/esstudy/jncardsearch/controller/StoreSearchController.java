package com.esstudy.jncardsearch.controller;

import com.esstudy.jncardsearch.dto.StoreDetailResponse;
import com.esstudy.jncardsearch.dto.StoreSearchRequest;
import com.esstudy.jncardsearch.dto.StoreSearchResponse;
import com.esstudy.jncardsearch.service.BookmarkService;
import com.esstudy.jncardsearch.service.SearchRankService;
import com.esstudy.jncardsearch.service.StoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

//가맹점 조회

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/search")
public class StoreSearchController {
    private final StoreService storeService;
    private final SearchRankService searchRankService;
    private final BookmarkService bookmarkService;


    @GetMapping("/stores")
    public ResponseEntity<?> searchStores(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String sido,
            @RequestParam(required = false) String category,
            @RequestParam(defaultValue = "광주은행") String bank,
            @RequestParam(required = false) String cursor,
            @RequestParam(defaultValue = "10") int size) {
        //서비스에서 검색 메서드가져오기
        System.out.println(q);

        StoreSearchRequest request = new StoreSearchRequest(q, sido, category, bank, cursor, size);

        searchRankService.incrementScore(q);
        StoreSearchResponse result = storeService.search(request);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(result);
    }
    //집계는 동적으로 바뀔때 사용
    //시군필터와 카테고리 필터는 고정이므로 하드코딩이 맞음


    //인기검색어 조회
    @GetMapping("/rank")
    public ResponseEntity<?> getSearchRank(){
        List<String> result = searchRankService.getTopKeywords();
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(result);
    }

    //자동완성
    @GetMapping("/autocomplete")
    public ResponseEntity<?> getSearchAutocomplete(@RequestParam(required = false) String q){
        List<String> result = storeService.autoComplete(q);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(result);
    }

    //상세조회
    @GetMapping("/store/{storeId}")
    public ResponseEntity<StoreDetailResponse> getSearchStore(@AuthenticationPrincipal Long userId, @PathVariable Long storeId){
        StoreDetailResponse result = storeService.getStoreDetail(storeId);
        boolean isBookmarked = userId != null &&
                bookmarkService.isBookmarked(userId, storeId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(StoreDetailResponse.builder()
                        .storeId(result.getStoreId())
                        .storeName(result.getStoreName())
                        .sido(result.getSido())
                        .address(result.getAddress())
                        .category(result.getCategory())
                        .bank(result.getBank())
                        .avgRating(result.getAvgRating())
                        .reviewCount(result.getReviewCount())
                        .bookmarkCount(result.getBookmarkCount())
                        .isBookmarked(isBookmarked)
                        .build());
    }
}