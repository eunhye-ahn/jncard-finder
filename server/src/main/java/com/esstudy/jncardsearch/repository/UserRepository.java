package com.esstudy.jncardsearch.repository;

import com.esstudy.jncardsearch.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
