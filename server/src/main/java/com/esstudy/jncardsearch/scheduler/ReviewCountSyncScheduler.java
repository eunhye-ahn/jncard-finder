package com.esstudy.jncardsearch.scheduler;

import com.esstudy.jncardsearch.dto.ReviewCountDto;
import com.esstudy.jncardsearch.repository.ReviewRepository;
import com.esstudy.jncardsearch.service.StoreService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class ReviewCountSyncScheduler {
    private final ReviewRepository reviewRepository;
    private final StoreService storeService;

    @Scheduled(cron="0 0 3 * * *")
    public void syncReviewCount() {
        log.info("review count sync start");

        List<ReviewCountDto> counts = reviewRepository.countGroupByStoreId();

        for(ReviewCountDto dto : counts) {
            storeService.setReviewCount(String.valueOf(dto.getStoreId()), dto.getCount());
        }

        log.info("review count sync complete - {}", counts.size());
    }
}