package com.esstudy.jncardsearch.repository;

import com.esstudy.jncardsearch.domain.Review;
import com.esstudy.jncardsearch.domain.Store;
import com.esstudy.jncardsearch.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    boolean existsByUserAndStore(User user, Store store);

    List<Review> findByIdAndUser(Long id, User user);

    boolean existsByIdAndUser(Long id, User user);

    @Query("select r from Review r join fetch r.store where r.store = :store")
    List<Review> findAllByStore(Store store);

    @Query("select r from Review r join fetch r.store where r.user = :user")
    List<Review> findAllByUser(User user);
}
