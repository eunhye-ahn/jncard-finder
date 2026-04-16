package com.esstudy.jncardsearch.service;

import com.esstudy.jncardsearch.domain.Bookmark;
import com.esstudy.jncardsearch.domain.Store;
import com.esstudy.jncardsearch.domain.User;
import com.esstudy.jncardsearch.dto.BookmarkStatus;
import com.esstudy.jncardsearch.exception.CustomException;
import com.esstudy.jncardsearch.exception.ErrorCode;
import com.esstudy.jncardsearch.repository.BookmarkRepository;
import com.esstudy.jncardsearch.repository.StoreRepository;
import com.esstudy.jncardsearch.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BookmarkService {
    private final BookmarkRepository bookmarkRepository;
    private final UserRepository userRepository;
    private final StoreRepository storeRepository;
    private final StoreService storeService;

    //on/off 전환 동작 명시적 표현 : toggle
    @Transactional
    public BookmarkStatus toggleBookmark(Long userId, Long storeId){
        //유저조회
        User user = userRepository.findById(userId)
                .orElseThrow(()-> new CustomException(ErrorCode.USER_NOT_FOUND));

        //가맹점조회
        Store store = storeRepository.findById(storeId)
                .orElseThrow(()-> new CustomException(ErrorCode.STORE_NOT_FOUND));

        //북마크여부
        if(bookmarkRepository.existsByUserAndStore(user, store)){
            bookmarkRepository.deleteByUserAndStore(user, store);
            storeService.incrementBookmarkCount(String.valueOf(storeId),-1);
            return BookmarkStatus.REMOVED;
        } else {
            bookmarkRepository.save(Bookmark.builder()
                    .user(user)
                    .store(store)
                    .build());
            storeService.incrementBookmarkCount(String.valueOf(storeId),+1);
            return BookmarkStatus.ADDED;
        }
    }

    //북마크 여부 조회 - 상세조회 컨트롤러에서 호출
    public boolean isBookmarked(Long userId, Long storeId){
        User user = userRepository.findById(userId)
                .orElseThrow(()-> new CustomException(ErrorCode.USER_NOT_FOUND));
        Store store = storeRepository.findById(storeId)
                .orElseThrow(()->new CustomException(ErrorCode.STORE_NOT_FOUND));
        return bookmarkRepository.existsByUserAndStore(user, store);
    }
}
