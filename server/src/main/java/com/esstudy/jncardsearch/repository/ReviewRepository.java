package com.esstudy.jncardsearch.repository;

import com.esstudy.jncardsearch.domain.Review;
import com.esstudy.jncardsearch.domain.Store;
import com.esstudy.jncardsearch.domain.User;
import com.esstudy.jncardsearch.dto.ReviewCountDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long>, ReviewRepositoryCustom{
    boolean existsByUserAndStore(User user, Store store);

    List<Review> findByIdAndUser(Long id, User user);

    boolean existsByIdAndUser(Long id, User user);

    @Query("select r from Review r join fetch r.store where r.user = :user")
    List<Review> findAllByUser(User user);

    @Query("select r.store.id as storeId, count(r) as count from Review r group by r.store.id")
    List<ReviewCountDto> countGroupByStoreId();

    @Query("select r from Review r join fetch r.user where r.store = :store order by r.createdAt desc")
    List<Review> findAllByStoreOrderByCreatedAt(Store store);
}
