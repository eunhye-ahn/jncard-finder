package com.esstudy.jncardsearch.scheduler;

import com.esstudy.jncardsearch.dto.BookmarkCountDto;
import com.esstudy.jncardsearch.repository.BookmarkRepository;
import com.esstudy.jncardsearch.service.StoreService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

//북마크 수 db,es 동기화 스케줄러
@Component
@Slf4j
@RequiredArgsConstructor
public class BookmarkCountSyncScheduler {
    private final BookmarkRepository bookmarkRepository;
    private final StoreService storeService;

    @Scheduled(cron="0 0 3 * * *")
    public void syncBookmarksCount() {
        log.info("bookmark count sync start");

        //db에서 storeId별 count 집계
        List<BookmarkCountDto> counts = bookmarkRepository.countGroupByStoreId();

        //es bookmarkCount -> db결과로 덮어쓰기
        for(BookmarkCountDto dto : counts) {
            storeService.setBookmarkCount(String.valueOf(dto.getStoreId()), dto.getCount());
        }

        log.info("bookmark count sync complete - {}", counts.size());
    }
}
