package com.ll.exam.final__2022_10_08.api.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@Getter
@AllArgsConstructor
public enum EventType {
    UNHANDLED_EXCEPTION(BAD_REQUEST, 9999, "예상치 못한 예외입니다.");
    private final HttpStatus httpStatus;
    private final int statusCode;
    private final String message;
}
