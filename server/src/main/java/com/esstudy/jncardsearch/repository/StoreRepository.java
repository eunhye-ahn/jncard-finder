package com.esstudy.jncardsearch.repository;

import com.esstudy.jncardsearch.domain.Store;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StoreRepository extends JpaRepository<Store, Long> {
    List<Store> findByBank(String bank);
}
