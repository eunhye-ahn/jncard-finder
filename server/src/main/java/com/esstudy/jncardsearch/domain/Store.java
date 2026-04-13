package com.esstudy.jncardsearch.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Table(name = "store")
@Entity
@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class Store {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "store_name", nullable = false)
    private String storeName;

    private String sido;

    private String address;

    private String category;

    private String bank;

    @Column(name="avg_rating")
    private Float avgRating = 0.0f; //평균평점

    @Column(name="review_count")
    private Integer reviewCount = 0; //리뷰수
}
