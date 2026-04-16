package com.esstudy.jncardsearch.domain;

import org.springframework.data.annotation.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.annotations.*;
import org.springframework.data.elasticsearch.core.suggest.Completion;

/**
 * es : 검색특화 DB
 * -처음부터 검색용 인덱스를 만들어둠
 * -빠르고 정확
 * -한글 형태소 분석지원 FieldType.Text : 형태소 분석해서 검색처리
 *
 * es 구조
 * -index : db
 * -document : 행
 * -fieldId : 속성
 * -mapping : 스키마
 *
 * es 기능 : 검색(주요) / 자동완성(지원) - CompletionFieldId
 *
 * ES Document : 자바에 인덱스 구조 선언docker-compose restart kibana
 *
 * ES 인덱스 매핑 : ES 자체에도 인덱스 구조 알림
 *              - @Document로 스프링 부트가 자동 생성 (nori분석기 설정X)
 *              or Kibana에서 직접 생성 (nori분석기 직접 설정 가능) - 자동완성, 한글검색기능이 있다면
 *
 *              -> 매핑 : 인덱스에게 ES구조를 알림 + nori분석기 설정
 */

@Document(indexName = "stores")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StoreDocument {
    @Id
    private String id;

    @Field(type = FieldType.Long)
    private Long storeId;

    @Field(type= FieldType.Search_As_You_Type, analyzer = "nori_analyzer")
    private String storeName;

    @Field(type=FieldType.Keyword)
    private String sido;

    @Field(type=FieldType.Text, analyzer = "nori_analyzer")
    private String address;

    @MultiField(mainField = @Field(type = FieldType.Keyword),
    otherFields = {@InnerField(suffix = "text", type = FieldType.Text, analyzer = "nori_analyzer")})
    private String category;

    @Field(type = FieldType.Keyword)
    private String bank;

    //리뷰작성되면 avgRating, reviewCount 업데이트
    @Field(type=FieldType.Float)
    private Float avgRating = 0.0f; //평균평점

    @Field(type = FieldType.Integer)
    private Integer reviewCount = 0; //리뷰수

    @Field(type = FieldType.Integer)
    private Integer bookmarkCount = 0; //찜 수
}
