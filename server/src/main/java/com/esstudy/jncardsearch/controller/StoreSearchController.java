package com.esstudy.jncardsearch.controller;

import com.esstudy.jncardsearch.dto.StoreSearchRequest;
import com.esstudy.jncardsearch.dto.StoreSearchResponse;
import com.esstudy.jncardsearch.service.StoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class StoreSearchController {
    private final StoreService storeService;


    @GetMapping("/api/search/stores")
    public ResponseEntity<?> searchStores(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String sido,
            @RequestParam(required = false) String cursor,
            @RequestParam(defaultValue = "10") int size){
        //서비스에서 검색 메서드가져오기
        System.out.println(q);

        StoreSearchRequest request = new StoreSearchRequest(q,sido,cursor,size);
            StoreSearchResponse result = storeService.search(request);

            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(result);
    }
}