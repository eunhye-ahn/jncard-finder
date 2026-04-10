package com.esstudy.jncardsearch.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    //es검색
    //커서조작시
    INVALID_CURSOR(HttpStatus.BAD_REQUEST, "유효하지않는 커서입니다"),

    //페이징 이건 서비스에서 처리

    //공통
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버에 오류가 발생했습니다.");

    private final HttpStatus status;
    private final String message;

    ErrorCode(final HttpStatus status, final String message) {
        this.status = status;
        this.message = message;
    }
}
