package com.esstudy.jncardsearch.infrastructure;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import com.esstudy.jncardsearch.domain.Store;
import com.esstudy.jncardsearch.domain.StoreDocument;
import com.esstudy.jncardsearch.repository.StoreRepository;
import com.esstudy.jncardsearch.repository.StoreSearchRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * [WHAT] 파싱한 데이터 ES에 저장
 * [WHY]
 * [흐름]
 *
 * 파일 종류별 읽는 방법
 * .txt / .csv → FileReader
 * .xlsx / .xls → InputStream + POI
 * .pdf → InputStream + PDFBox
 * 이미지 → InputStream + ImageIO
 */

/**
 * xlsx 파일
 * = zip 압축파일이라 내부에 xml파일들이 있음
 *      -> xml은 기본적으로 utf-8 인코딩
 * POI의 역할
 *  Workbook workbook = new XSSFWorkbook(is))
 *         -> 내부적으로 xml파싱 + utf-8 디코딩 자동 처리
 *         -> 개발할때 cell.getStringCellValue()만 호출하면 됨
 *  new InputStreamReader(fis, "UTF-8") //개발자가 인코딩 직접 처리
 *  new XSSWorkbook(is) //라이브러리가 알아서 처리
 */

@Component
@RequiredArgsConstructor
@Slf4j
public class ExcelStoreLoader {

    // ES 저장소 주입 -> 파싱한 데이터를 ES에 저장하기 위해
    private final StoreSearchRepository storeSearchRepository;
    private final StoreRepository storeRepository;
    private final JdbcTemplate jdbcTemplate;
    private final ElasticsearchOperations elasticsearchOperations;
    private final ElasticsearchClient elasticsearchClient;

    public void loadAll(String filePath) throws IOException {
        long totalStart = System.currentTimeMillis();
        disableRefresh();
        loadStores(filePath,"광주은행","광주은행");
        loadStores(filePath,"농협은행","농협은행");
        loadStores(filePath, "성능테스트", "성능테스트");
        enableRefresh();
        long totalEnd = System.currentTimeMillis();
        log.info("[all data saved] : {}ms", totalEnd - totalStart);
    }

    public void loadStores(String filePath, String sheetName, String bank) throws IOException {
        // 순서보장, 중복저장허용

        long t1 = System.currentTimeMillis();

        //카테고리 맵 로드 - XSSFWorkbook => 행 수 적으므로
        Map<String, String> categories;
        try (InputStream is = getClass().getResourceAsStream(filePath);
             Workbook workbook = new XSSFWorkbook(is)) {
            categories = loadCategoryMap(workbook);
        }

        //SAX 스트리밍으로 데이터 시트 파싱 => 대규모 데이터 시, OOM 상황 방지
        List<Store> stores = new ArrayList<>();

        // 파일 읽기
        try (InputStream is = getClass().getResourceAsStream(filePath);
             Workbook workbook = new XSSFWorkbook(is)) {

            Map<String,String> categories = loadCategoryMap(workbook);

            Sheet sheet = workbook.getSheet(sheetName);

            // 엑셀 행 읽기
            for (int i = 3; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                String rawCategoy = getCellValue(row,6).replaceAll("\\s+", "");
                String category = categories.getOrDefault(rawCategoy,"기타");

                //저장 순서 엑셀파싱>rdb저장>es저장 : rdb의 생성된 id를
                // es의 storeId(정렬용/rdb연결용)에 저장하기위해
                Store store = Store.builder()
                                .sido(getCellValue(row, 1))
                                .storeName(getCellValue(row, 3))
                                .address(getCellValue(row, 4))
                                .category(category)
                                .bank(bank)
                                // 리뷰 작성되면 업데이트
                                .avgRating(0.0f)
                                .reviewCount(0)
                                .bookmarkCount(0)
                        .build();
                stores.add(store);
            }

            long t2 = System.currentTimeMillis();
            log.info("[excel parsing done] {}ms, {}건", t2 - t1, stores.size());

            // DB INSERT -
            //1. 전체 insert
//            storeRepository.saveAll(stores);
            // 2. 청크단위 insert
//            int chunkSize = 1000;
//            for(int i=0;i<stores.size();i+=chunkSize){
//                List<Store> chunk = stores.subList(i,Math.min(i+chunkSize, stores.size()));
//                storeRepository.saveAll(chunk);
//                log.info("[db save chunk] {}/{}", Math.min(i+chunkSize, stores.size()), chunk.size());
//            }


            //3. jdbc batchUpdate로 insert
            String sql = """
                        INSERT INTO store(store_name, sido, address, category, bank,
                                                  avg_rating, review_count, bookmark_count)
                                VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                    """;

            jdbcTemplate.batchUpdate(sql, stores, 1000, (ps, s) -> {
                ps.setString(1, s.getStoreName());
                ps.setString(2, s.getSido());
                ps.setString(3, s.getAddress());
                ps.setString(4, s.getCategory());
                ps.setString(5, s.getBank());
                ps.setFloat(6, s.getAvgRating());
                ps.setInt(7, s.getReviewCount());
                ps.setInt(8, s.getBookmarkCount());
            });
            long t3 = System.currentTimeMillis();
            log.info("[DB save done] {}ms", t3 - t2);


            List<Store> savedStores = storeRepository.findByBank(bank);
            long t3_1 = System.currentTimeMillis();
            log.info("[{}] findByBank done -> {}ms, {}건",bank, t3_1-t3, savedStores.size());

            List<StoreDocument> storeEs = savedStores.stream()
                    .map(s-> StoreDocument.builder()
                            .id(String.valueOf(s.getId())) //redinex시, 아이디 덮어쓰기
                            .storeId(s.getId())
                            .storeName(s.getStoreName())
                            .sido(s.getSido())
                            .address(s.getAddress())
                            .category(s.getCategory())
                            .bank(s.getBank())
                            .avgRating(s.getAvgRating())
                            .reviewCount(s.getReviewCount())
                            .bookmarkCount(s.getBookmarkCount())
                            .build()
                    )
                    .toList();
            long t3_2 = System.currentTimeMillis();
            log.info("[{}] stream mapping done -> {}ms",bank, t3_2-t3_1);


            // ES INSERT
            int chunkSize = 5000;
            for(int i=0;i<stores.size();i+=chunkSize){
                List<StoreDocument> chunk = storeEs.subList(i,Math.min(i+chunkSize, storeEs.size()));
                storeSearchRepository.saveAll(chunk);
                log.info("[es indexing chunk] {}/{}", Math.min(i+chunkSize, storeEs.size()), chunk.size());
            }

            long t4 = System.currentTimeMillis();
            log.info("[ES indexing done] {}ms", t4 - t3_2);

            log.info("[{}] bank {}ms", bank, t4 - t1);
        }
    }

    // excel 데이터 전처리
    //1. excel데이터 타입 string으로 통일
    private String getCellValue(Row row, int column) {
        Cell cell = row.getCell(column);
        if (cell == null) return "";

        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue().trim(); //앞뒤공백제거
            case NUMERIC -> String.valueOf((long)cell.getNumericCellValue());
            default -> "";
        };
    }

    //3. 카테고리 매핑
    private Map<String,String> loadCategoryMap(Workbook workbook){
        Sheet sheet = workbook.getSheet("업종-매핑");
        Map<String,String> map = new HashMap<>();

        for(int i = 1; i <= sheet.getLastRowNum(); i++){
            Row row = sheet.getRow(i);
            if (row == null) continue;
            //중간공백제거
            String original = getCellValue(row,0).replaceAll("\\s+", "");
            String category = getCellValue(row,1);

            if(!original.isEmpty()){
                map.put(original,category);
            }
        }
        return map;
    }

    //es refresh 끄기
    private void disableRefresh() throws IOException{
        elasticsearchClient.indices().putSettings(r -> r
                .index("stores")
                .settings(s -> s.refreshInterval(t -> t.time("-1")))
        );
        log.info("[ES] refresh_interval disabled");
    }

    //es refresh 켜기
    private void enableRefresh() throws IOException{
        elasticsearchClient.indices().putSettings(r -> r
                .index("stores")
                .settings(s -> s.refreshInterval(t -> t.time("1s")))
        );
        elasticsearchOperations.indexOps(IndexCoordinates.of("stores")).refresh();
        log.info("[ES] refresh enabled");
    }
}