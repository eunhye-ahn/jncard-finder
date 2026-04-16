package com.esstudy.jncardsearch.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class BookmarkRequest {
    @NotNull
    private Long storeId;
}
