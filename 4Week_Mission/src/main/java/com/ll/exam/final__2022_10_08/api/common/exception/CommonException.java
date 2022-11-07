package com.ll.exam.final__2022_10_08.api.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public class CommonException extends RuntimeException{

    private final HttpStatus httpStatus;
    private final int code;
    private final String message;
}
