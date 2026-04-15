package com.esstudy.jncardsearch.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.query_dsl.TextQueryType;
import com.esstudy.jncardsearch.domain.StoreDocument;
import com.esstudy.jncardsearch.dto.StoreDetailResponse;
import com.esstudy.jncardsearch.dto.StoreSearchRequest;
import com.esstudy.jncardsearch.dto.StoreSearchResponse;
import com.esstudy.jncardsearch.exception.CustomException;
import com.esstudy.jncardsearch.exception.ErrorCode;
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

import java.lang.annotation.Native;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

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
 *
 * [cursor 흐름]
 * 1페이지 요청
 * → cursor 없음 → 처음부터 조회
 * → 마지막 문서 정렬값 → encodeCursor
 * → cursor: "WyIxLjAiLCAiMTIzIl0=" 응답
 *
 * 2페이지 요청
 * → cursor: "WyIxLjAiLCAiMTIzIl0=" 전달
 * → decodeCursor → ["1.0", "123"]
 * → search_after에 넣어서 다음 데이터 조회
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
                        .storeId(hit.getContent().getStoreId())
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

    //자동완성
    public List<String> autoComplete(String q) {
        NativeQuery query =  buildAutocompleteQuery(q);
        SearchHits<StoreDocument> hits = elasticsearchOperations.search(query, StoreDocument.class);

        return hits.getSearchHits()
                .stream()
                .map(hit -> hit.getContent().getStoreName())
                .distinct()
                .toList();
    }

    //자동완성 쿼리빌드
    private NativeQuery buildAutocompleteQuery(String q) {
        return NativeQuery.builder()
                .withQuery(qb -> qb
                        .multiMatch(mm -> mm
                                .query(q)
                                .type(TextQueryType.BoolPrefix)
                                .fields("storeName", "storeName._2gram", "storeName._3gram")
                        )
                )
                .withPageable(PageRequest.of(0,3))
                .build();
    }

    //ES 쿼리 빌드
    private NativeQuery buildQuery(StoreSearchRequest request) {
        System.out.println(request.toString());
        return NativeQuery.builder()
                .withQuery(q -> q       //es쿼리 시작
                        .bool(b-> {     //bool쿼리 시작 - 조건묶음
                            //검색어 -> storeName, address => text
                                if(StringUtils.hasText(request.getQ())){
                                    //검색어(점수반영) must type -> multiMatch쿼리로 작성
                                    b.must(m->m
                                            .multiMatch(mm -> mm
                                                    .query(request.getQ())
                                                    .fields("storeName^3","address^1","category^2")));
                                }
                                if(StringUtils.hasText(request.getSido())){
                                    //시군필터(점수무관) filter type -> term쿼리로 작성
                                    b.filter(f->f
                                            .term(t->t
                                                    .field("sido")
                                                    .value(request.getSido())));
                                }
                                if(StringUtils.hasText(request.getCategory())){
                                    b.filter(f->f
                                            .term(t->t
                                                    .field("category")
                                                    .value(request.getCategory())));
                                }
                                if(StringUtils.hasText(request.getBank())){
                                    b.filter(f->f
                                    .term(t->t
                                            .field("bank")
                                            .value(request.getBank())));
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
    //클라이언트가 보낸 url => cursor="문자열"
    //디코딩해서 json으로 변환
    //json파싱해서 배열로 생성 > searchAfter에 넣기 위해 - ES가 인식할 수 있는,
    private List<Object> decodeCursor(String cursor) {
        //base644디코딩
        try {
            String decoded = new String(Base64.getDecoder().decode(cursor));
            //문자열변환
            String[] parts = decoded.split(",");
            return List.of(
                    Double.parseDouble(parts[0]), //string->double
                    Long.parseLong(parts[1]) //string->long
            );
        }catch(Exception e){
            throw new CustomException(ErrorCode.INVALID_CURSOR);
        }
    }

    //base64인코딩
    //search_after 값 : ["_score", "keyword"] 같은 배열
    //클라에 전달하기 위해 json 변환
    //url로 전달하기 불편 -> 인코딩해서 문자열로 변환
    private String encodeCursor(SearchHit<StoreDocument> lastHit) {
        String raw = lastHit.getScore()+","+lastHit.getContent().getStoreId();
        return Base64.getEncoder().encodeToString(raw.getBytes());
    }

    public StoreDetailResponse getStoreDetail(Long storeId) {
        //es조회
        NativeQuery query = NativeQuery.builder()
                .withQuery(q->q
                        .term(t->t
                                .field("storeId")
                                .value(storeId)
                        )
                )
                .build();

        SearchHits<StoreDocument> hits = elasticsearchOperations.search(query, StoreDocument.class);

        if(hits.isEmpty()){
            throw new CustomException(ErrorCode.STORE_NOT_FOUND);
        }

        StoreDocument doc = hits.getSearchHits().get(0).getContent();

        //dto로 변환
        return StoreDetailResponse.builder()
                .storeId(doc.getStoreId())
                .storeName(doc.getStoreName())
                .sido(doc.getSido())
                .address(doc.getAddress())
                .category(doc.getCategory())
                .bank(doc.getBank())
                .avgRating(doc.getAvgRating())
                .reviewCount(doc.getReviewCount())
                .bookmarkCount(doc.getBookmarkCount())
                .build();
    }
}
