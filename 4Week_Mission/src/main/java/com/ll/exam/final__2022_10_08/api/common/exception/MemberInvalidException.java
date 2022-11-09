package com.ll.exam.final__2022_10_08.api.common.exception;

public class MemberInvalidException extends CommonException{

    public MemberInvalidException(ExceptionType exceptionType, Long memberId) {
        super(exceptionType.getHttpStatus(), exceptionType.getStatusCode(), memberId + exceptionType.getMessage());
    }
    public MemberInvalidException(ExceptionType exceptionType) {
        super(exceptionType.getHttpStatus(), exceptionType.getStatusCode(), exceptionType.getMessage());
    }
}
