package com.ll.exam.final__2022_10_08.api.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@Getter
@AllArgsConstructor
public enum ExceptionType {
    // member 관련 Exception
    MEMBER_NOT_FOUND(NOT_FOUND, 100, "번째 회원을 찾을 수 없습니다."),
    MEMBER_USERNAME_NOT_FOUND(NOT_FOUND, 101, "아이디가 존재하지 않습니다."),
    MEMBER_PASSWORD_MISMATCH(BAD_REQUEST, 102, "비밀번호가 일치하지 않습니다."),

    // 알 수 없는 Exception
    UNHANDLED_EXCEPTION(BAD_REQUEST, 9999, "예상치 못한 예외입니다.");

    private final HttpStatus httpStatus;
    private final int statusCode;
    private final String message;
}
