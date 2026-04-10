package com.esstudy.jncardsearch.global;

import com.esstudy.jncardsearch.infrastructure.ExcelStoreLoader;
import com.esstudy.jncardsearch.repository.StoreRepository;
import com.esstudy.jncardsearch.repository.StoreSearchRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * [WHAT] 애플리케이션 시작 시, ES에 가맹점 데이터 적재
 * [WHY] 서버 재시작마다 중복저장되는 것을 방지하고,
 *      최초 1회만 엑셀 데이터를 ES에 로드하기 위해
 * [흐름]
 * 1. 애플리케이션 구동 완료 시점에 run() 자동 실행 - ApplicationRunner
 * 2. ES에 이미 데이터가 있으면 -> 적재 스킵
 * 3. ES가 비어있으면 -> 엑셀 파일 파싱 후 전략 저장
 */

@Component
@RequiredArgsConstructor
@Slf4j
public class DataLoader implements ApplicationRunner {
    private final ExcelStoreLoader excelStoreLoader;
    private final StoreSearchRepository storeSearchRepository;
    private final StoreRepository storeRepository;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        //중복저장방지
        if(storeSearchRepository.count() > 0 && storeRepository.count() > 0) {
            log.info("Almost ES data in");
            return;
        }
        excelStoreLoader.loadStores("/data/stores.xlsx");
    }
}
