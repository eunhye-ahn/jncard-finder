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
import java.util.stream.Collectors;

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

    public void loadStores(String filePath) throws IOException {

        // 순서보장, 중복저장허용
        List<Store> stores = new ArrayList<>();

        // 파일 읽기
        try (InputStream is = getClass().getResourceAsStream(filePath);
             Workbook workbook = new XSSFWorkbook(is)) {

            Sheet sheet = workbook.getSheet("광주은행");

            // 엑셀 행 읽기
            for (int i = 3; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                //저장 순서 엑셀파싱>rdb저장>es저장 : rdb의 생성된 id를
                // es의 storeId(정렬용/rdb연결용)에 저장하기위해
                Store store = Store.builder()
                                .sido(getCellValue(row, 1))
                                .storeName(getCellValue(row, 3))
                                .address(getCellValue(row, 4))
                                .category(getCellValue(row, 5))
                                // 리뷰 작성되면 업데이트
                                .avgRating(0.0f)
                                .reviewCount(0)
                        .build();
                stores.add(store);
            }
            storeRepository.saveAll(stores);
            log.info("DB 저장 완료 : {}건", stores.size());

            // es에 저장
            List<StoreDocument> storeEs = stores.stream()
                    .map(s-> StoreDocument.builder()
                            .storeId(s.getId())
                            .storeName(s.getStoreName())
                            .sido(s.getSido())
                            .address(s.getAddress())
                            .category(s.getCategory())
                            .avgRating(s.getAvgRating())
                            .reviewCount(s.getReviewCount())
                            .build()
                    )
                    .toList();
            storeSearchRepository.saveAll(storeEs);
            log.info("ES 저장 완료 : {}건", storeEs.size());
        }
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