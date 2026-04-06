package com.esstudy.jncardsearch.repository;

import com.esstudy.jncardsearch.domain.StoreDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * [WHAT] ES CRUD 기능을 자동으로 제공받는 인터페이스 (ElasticsearchRepository를 상속)
 * [WHY]
 * [흐름] JpaRepository와 동일한 개념(JPA → PostgreSQL, ElasticsearchRepository → ES)
 */

public interface StoreSearchRepository extends ElasticsearchRepository<StoreDocument, Long> {
}
