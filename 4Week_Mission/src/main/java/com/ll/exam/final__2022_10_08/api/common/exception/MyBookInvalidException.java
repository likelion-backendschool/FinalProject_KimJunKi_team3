package com.ll.exam.final__2022_10_08.api.common.exception;

public class MyBookInvalidException extends CommonException{

    public MyBookInvalidException(ExceptionType exceptionType, Long memberId) {
        super(exceptionType.getHttpStatus(), exceptionType.getStatusCode(), memberId + exceptionType.getMessage());
    }
    public MyBookInvalidException(ExceptionType exceptionType) {
        super(exceptionType.getHttpStatus(), exceptionType.getStatusCode(), exceptionType.getMessage());
    }
}
