package com.esstudy.jncardsearch.controller;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import com.esstudy.jncardsearch.domain.StoreDocument;
import com.esstudy.jncardsearch.dto.StoreSearchRequest;
import com.esstudy.jncardsearch.dto.StoreSearchResponse;
import com.esstudy.jncardsearch.service.StoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/search")
public class StoreSearchController {
    private final StoreService storeService;
    private final ElasticsearchClient elasticsearchClient;


    @GetMapping("/stores")
    public ResponseEntity<?> searchStores(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String sido,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String cursor,
            @RequestParam(defaultValue = "10") int size) {
        //서비스에서 검색 메서드가져오기
        System.out.println(q);

        StoreSearchRequest request = new StoreSearchRequest(q, sido, category, cursor, size);
        StoreSearchResponse result = storeService.search(request);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(result);
    }
    //집계는 동적으로 바뀔때 사용
    //시군필터와 카테고리 필터는 고정이므로 하드코딩이 맞음
}