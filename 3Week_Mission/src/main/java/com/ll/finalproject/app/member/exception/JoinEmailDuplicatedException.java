package com.ll.finalproject.app.member.exception;

public class JoinEmailDuplicatedException extends RuntimeException{
    public JoinEmailDuplicatedException(String message) {
        super(message);
    }
}
