package com.esstudy.jncardsearch.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import com.esstudy.jncardsearch.domain.StoreDocument;
import com.esstudy.jncardsearch.dto.StoreSearchRequest;
import com.esstudy.jncardsearch.dto.StoreSearchResponse;
import com.esstudy.jncardsearch.repository.StoreSearchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Base64;
import java.util.List;

/**
 * [검색 실행 흐름]
 * buildQuery(request)로 쿼리완성
 * > elasticsearchOperations.search(query, StoreDocument.class)
 * > ES 서버에 HTTP 요청 날라감
 * > ES가 인덱스에서 매칭 문서 찾고 score 계산
 * > SearchHits<StoreDocument> 로 결과 반환
 * > hits.getSearchHits() 로 List<SearchHit> 꺼냄
 * > 각 hit에서 getContent()로 StoreDocument 꺼냄
 * > DTO로 변환해서 응답
 */
@Service
@RequiredArgsConstructor
public class StoreService {
    private final ElasticsearchOperations elasticsearchOperations;

    public StoreSearchResponse search(StoreSearchRequest request) {

        //검색조건 빌드
        NativeQuery query = buildQuery(request);
        //검색조건 실행
        SearchHits<StoreDocument> hits = elasticsearchOperations.search(query, StoreDocument.class);
        List<SearchHit<StoreDocument>> hitList = hits.getSearchHits();

        //nextCursor 만들기 : hasNext가 true일 때 마지막 hit의 score + id를 인코딩한 값

        //hasNext판별
        boolean hasNextPage = hitList.size()> request.getSize();

        //+1 제거
        if(hasNextPage){
            hitList = hitList.subList(0, request.getSize());
        }

        //nextCursor 생성
        String nextCursor = null;
        if(hasNextPage){
            SearchHit<StoreDocument> lastHit = hitList.get(hitList.size() - 1);
            nextCursor = encodeCursor(lastHit);
        }

        //dto변환
        List<StoreSearchResponse.StoreDto> storeDtos = hitList.stream()
                .map(hit -> StoreSearchResponse.StoreDto.builder()
                        .id(hit.getContent().getId())
                        .storeName(hit.getContent().getStoreName())
                        .sido(hit.getContent().getSido())
                        .address(hit.getContent().getAddress())
                        .build()
                )
                .toList();
        StoreSearchResponse result = StoreSearchResponse.builder()
                .stores(storeDtos)
                .nextCursor(nextCursor)
                .hasNext(hasNextPage)
                .build();

        return result;
    }

    //ES 쿼리 빌드
    private NativeQuery buildQuery(StoreSearchRequest request) {
        System.out.println(request.toString());
        return NativeQuery.builder()
                .withQuery(q -> q       //es쿼리 시작
                        .bool(b-> {     //bool쿼리 시작 - 조건묶음
                            //검색어 -> storeName, address => text
                                if(StringUtils.hasText(request.getQ())){
                                    //검색어(점수반영)
                                    b.must(m->m
                                            .multiMatch(mm -> mm
                                                    .query(request.getQ())
                                                    .fields("storeName","address","category")));
                                }
                                if(StringUtils.hasText(request.getSido())){
                                    //시군필터(점수무관)
                                    b.filter(f->f
                                            .term(t->t
                                                    .field("sido")
                                                    .value(request.getSido())));
                                }
                                //검색없으면 전체조회
                                if(!StringUtils.hasText(request.getQ()) && !StringUtils.hasText(request.getSido()))
                                {
                                    b.must(m->m.matchAll(ma->ma));
                                }
                                return b;
                        }))
                //커서 있으면 > 다음데이터부터
                //커서 없으면 > 처음부터 - 첫요청
                .withSearchAfter(
                        StringUtils.hasText(request.getCursor())
                                ? decodeCursor(request.getCursor())
                                : null)
                .withSort(Sort.by(
                        Sort.Order.desc("_score"),
                        Sort.Order.asc("storeId")
                ))
                //offset은 page가 의미있음
                //cursor는 시작위치를 0고정, <- 커서담당으로 정하므로-withSearchAfter
                .withPageable(PageRequest.of(0, request.getSize()+1))
                //왜 +1을 해주냐? hasNext를 판별하기 위해
                .build();
    }

    //시작위치 설정
    private List<Object> decodeCursor(String cursor) {
        //base644디코딩
        String decoded = new String(Base64.getDecoder().decode(cursor));

        //문자열변환
        String[] parts = decoded.split(",");

        return List.of(
               Double.parseDouble(parts[0]),
               parts[1]
        );
    }

    //base64인코딩
    private String encodeCursor(SearchHit<StoreDocument> lastHit) {
        String raw = lastHit.getScore()+","+lastHit.getContent().getId();
        return Base64.getEncoder().encodeToString(raw.getBytes());
    }
}
