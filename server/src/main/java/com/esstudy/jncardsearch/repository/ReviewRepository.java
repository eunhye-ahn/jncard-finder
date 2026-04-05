package com.esstudy.jncardsearch.repository;

import com.esstudy.jncardsearch.domain.Review;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, Long> {
}
