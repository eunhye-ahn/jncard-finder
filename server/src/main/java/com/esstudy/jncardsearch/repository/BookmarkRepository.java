package com.esstudy.jncardsearch.repository;

import com.esstudy.jncardsearch.domain.Bookmark;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {
}
