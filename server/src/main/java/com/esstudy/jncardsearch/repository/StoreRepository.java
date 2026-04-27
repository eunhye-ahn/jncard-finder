package com.esstudy.jncardsearch.repository;

import com.esstudy.jncardsearch.domain.Store;
import com.esstudy.jncardsearch.dto.StoreCountDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface StoreRepository extends JpaRepository<Store, Long> {
    List<Store> findByBank(String bank);

    @Query(value = """
    SELECT s.id as storeId,
           COUNT(DISTINCT b.id) as bookmarkCount,
           COUNT(DISTINCT r.id) as reviewCount,
            COALESCE(ROUND(AVG(r.rating)::numeric,1), 0)  AS avgRating
    FROM Store s
    LEFT JOIN Bookmark b ON b.store_id = s.id
    LEFT JOIN Review r ON r.store_id = s.id
    GROUP BY s.id
    """, nativeQuery = true)
    List<StoreCountDto> findAllStoreCounts();
}
