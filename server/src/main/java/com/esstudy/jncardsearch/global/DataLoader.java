package com.esstudy.jncardsearch.global;

import com.esstudy.jncardsearch.infrastructure.ExcelStoreLoader;
import com.esstudy.jncardsearch.repository.*;
import com.esstudy.jncardsearch.service.StoreSyncService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.datafaker.Faker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * [WHAT] 커맨드라인러너
 *
 * 애플리케이션 시작 시, ES에 가맹점 데이터 적재
 * [WHY] 서버 재시작마다 중복저장되는 것을 방지하고,
 *      최초 1회만 엑셀 데이터를 ES에 로드하기 위해
 *
 *      더미데이터 삽입, 캐시 워밍업, 스케줄러초기화, 설정값 검증
 * [흐름]
 * 1. 애플리케이션 구동 완료 시점에 run() 자동 실행
 * 2. ES에 이미 데이터가 있으면 -> 적재 스킵
 * 3. ES가 비어있으면 -> 엑셀 파일 파싱 후 전략 저장
 */

@Component
@RequiredArgsConstructor
@Slf4j
public class DataLoader implements CommandLineRunner {
    private final ExcelStoreLoader excelStoreLoader;
    private final StoreSearchRepository storeSearchRepository;
    private final StoreRepository storeRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ReviewRepository reviewRepository;
    private final BookmarkRepository bookmarkRepository;
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    private StoreSyncService syncService;

    // ApplicationRunner는 객체를 인자로 받음
    @Override
    public void run(String... args) throws Exception {

        if (storeSearchRepository.count() == 0 && storeRepository.count() == 0) {
            excelStoreLoader.loadAll("/data/stores.xlsx");
        } else {
            log.info("Almost ES data in");
        }

        loadUser();
        loadReviewsAndBookmarks();

        //syncService.syncCountsToES();
    }

    private void loadUser() {
        log.info("Loading user");
        if (userRepository.count() > 0) return;

        Faker faker = new Faker(new Locale("ko"));

        List<Integer> userIndexes = IntStream.range(0, 10000)
                .boxed()
                .collect(Collectors.toList());

        jdbcTemplate.batchUpdate(
                "INSERT INTO users (name, email, password, role, created_at) VALUES (?, ?, ?, ?, NOW())",
                userIndexes, 500,
                (ps, i) -> {
                    ps.setString(1, faker.name().fullName());
                    ps.setString(2, "user" + i + "@test.com");
                    ps.setString(3, "$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy");
                    ps.setString(4, "ROLE_USER");
                }
        );

        log.info("[user data load] : {}", userIndexes.size());
    }

    private void loadReviewsAndBookmarks() {
        log.info("Loading review");
        if (reviewRepository.count() > 0 && bookmarkRepository.count()>0) return;

        Faker faker = new Faker(new Locale("ko"));

        // Store 전체 객체 대신 ID만 조회 (10만개 객체 메모리 적재 방지)
        List<Long> userIds = jdbcTemplate.queryForList("SELECT id FROM users", Long.class);
        List<Long> storeIds = jdbcTemplate.queryForList("SELECT id FROM store", Long.class);

        Collections.shuffle(userIds);
        Collections.shuffle(storeIds);

        // 리뷰: 인덱스 기반으로 중복 없이 생성
        List<Integer> reviewIndexes = IntStream.range(0, 100000)
                .boxed()
                .collect(Collectors.toList());

        List<String> reviewContents = List.of(
                "분위기가 정말 좋아요!",
                "음식이 맛있고 서비스도 친절해요.",
                "가격 대비 만족스러워요.",
                "또 방문하고 싶은 곳이에요.",
                "직원분들이 너무 친절하셨어요.",
                "청결하고 쾌적한 환경이었어요.",
                "웨이팅이 있었지만 기다릴 만한 가치가 있어요.",
                "주차하기 편리해서 좋았어요.",
                "메뉴가 다양해서 선택하기 좋아요.",
                "혼밥하기에도 편한 분위기예요.",
                "데이트 코스로 강추해요!",
                "가족 모임으로 딱 좋은 곳이에요.",
                "재료가 신선하고 양도 푸짐해요.",
                "포장도 친절하게 해주셔서 감사해요.",
                "단골이 될 것 같아요."
        );

        jdbcTemplate.batchUpdate(
                "INSERT INTO review (user_id, store_id, content, rating, created_at) VALUES (?, ?, ?, ?, NOW())",
                reviewIndexes, 500,
                (ps, i) -> {
                    ps.setLong(1, userIds.get(i % userIds.size()));
                    ps.setLong(2, storeIds.get((i * 7 + 3) % storeIds.size()));
                    ps.setString(3, reviewContents.get(faker.number().numberBetween(0, reviewContents.size())));
                    ps.setFloat(4, faker.number().numberBetween(2, 10) * 0.5f);
                }
        );
        log.info("[review data load] : 100000");

        log.info("Loading bookmark");

        // bookmark: review와 다른 소수로 분산해서 다른 조합 생성
        Collections.shuffle(userIds);
        Collections.shuffle(storeIds);

        List<Integer> bookmarkIndexes = IntStream.range(0, 100000)
                .boxed()
                .collect(Collectors.toList());

        jdbcTemplate.batchUpdate(
                "INSERT INTO bookmark (user_id, store_id, created_at) VALUES (?, ?, NOW())",
                bookmarkIndexes, 500,
                (ps, i) -> {
                    ps.setLong(1, userIds.get(i % userIds.size()));
                    ps.setLong(2, storeIds.get((i * 11 + 5) % storeIds.size()));
                }
        );
        log.info("[bookmark data load] : 100000");
    }
}