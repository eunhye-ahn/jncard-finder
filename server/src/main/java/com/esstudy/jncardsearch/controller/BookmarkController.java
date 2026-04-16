package com.esstudy.jncardsearch.controller;

import com.esstudy.jncardsearch.dto.BookmarkStatus;
import com.esstudy.jncardsearch.service.BookmarkService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

//북마크 생성,삭제, 조회

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/bookmarks")
public class BookmarkController {
    private final BookmarkService bookmarkService;

    @PostMapping("/{storeId}")
    public ResponseEntity<BookmarkStatus> togleBookmark(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long storeId) {
        System.out.println("userId = " + userId);

        BookmarkStatus result = bookmarkService.toggleBookmark(userId, storeId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(result);
    }


}
