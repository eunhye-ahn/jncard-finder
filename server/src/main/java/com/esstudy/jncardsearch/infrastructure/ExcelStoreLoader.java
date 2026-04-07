package com.esstudy.jncardsearch.infrastructure;

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
import org.springframework.data.elasticsearch.core.suggest.Completion;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

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

@Component
@RequiredArgsConstructor
@Slf4j
public class ExcelStoreLoader {

    // ES 저장소 주입 -> 파싱한 데이터를 ES에 저장하기 위해
    private final StoreSearchRepository storeSearchRepository;
    private final StoreRepository storeRepository;

    public void loadStores(String filePath) throws IOException {

        // 순서보장, 중복저장허용
        List<StoreDocument> stores = new ArrayList<>();

        // 파일 읽기
        try (InputStream is = getClass().getResourceAsStream(filePath);
             Workbook workbook = new XSSFWorkbook(is)) {

            Sheet sheet = workbook.getSheet("광주은행");

            // 엑셀 행 읽기
            for (int i = 4; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                // 엑셀 행 데이터 > StoreDocument 변환
                StoreDocument store = StoreDocument.builder()
                        .sido(getCellValue(row, 2))
                        .storeName(getCellValue(row, 3))
                        .address(getCellValue(row, 4))
                        .category(getCellValue(row, 5))
                        // 자동완성 => Completion 타입에 미리 저장해두어야 함
                        .storeNameSuggest(new Completion(new String[]{getCellValue(row, 3)}))
                        // 리뷰 작성되면 업데이트
                        .avgRating(0.0f)
                        .reviewCount(0)
                        .build();

                stores.add(store);
            }
        }

        //document > entity 로 변환
        List<Store> storeEntities = stores.stream()
                .map(storeEntity -> Store.builder()
                        .storeName(storeEntity.getStoreName())
                        .sido(storeEntity.getSido())
                        .address(storeEntity.getAddress())
                        .category(storeEntity.getCategory())
                        .avgRating(storeEntity.getAvgRating())
                        .reviewCount(storeEntity.getReviewCount())
                        .build()
                ).toList();

        storeSearchRepository.saveAll(stores);
        storeRepository.saveAll(storeEntities);
        log.info("ES 저장 완료 : {}건", stores.size());
    }

    private String getCellValue(Row row, int column) {
        Cell cell = row.getCell(column);
        if (cell == null) return "";

        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue().trim();
            case NUMERIC -> String.valueOf((long)cell.getNumericCellValue());
            default -> "";
        };
    }
}