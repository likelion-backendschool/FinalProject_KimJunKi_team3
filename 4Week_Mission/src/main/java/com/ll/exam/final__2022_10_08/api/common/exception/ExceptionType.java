package com.ll.exam.final__2022_10_08.api.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Getter
@AllArgsConstructor
public enum EventType {
    // member 관련 exception
    MEMBER_NOT_FOUND(NOT_FOUND, 100, "번째 회원을 찾을 수 없습니다.");
    UNHANDLED_EXCEPTION(BAD_REQUEST, 9999, "예상치 못한 예외입니다.");
    
    private final HttpStatus httpStatus;
    private final int statusCode;
    private final String message;
}
