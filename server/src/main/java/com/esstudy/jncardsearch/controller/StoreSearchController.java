package com.esstudy.jncardsearch.controller;

import com.esstudy.jncardsearch.dto.StoreSearchRequest;
import com.esstudy.jncardsearch.dto.StoreSearchResponse;
import com.esstudy.jncardsearch.service.StoreService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name="가맹점검색", description = "ES 기반 가맹점 검색 API")
@RestController
@RequiredArgsConstructor
public class StoreSearchController {
    private final StoreService storeService;

    @GetMapping("/api/search/stores")
    public ResponseEntity<?> searchStores(
            @ModelAttribute StoreSearchRequest request
            ){
        //서비스에서 검색 메서드가져오기
        StoreSearchResponse result = storeService.search(request);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(result);
    }
}
