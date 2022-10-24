package com.ll.finalproject.app.member.exception;

public class PasswordNotSameException extends RuntimeException{
    public PasswordNotSameException(String message) {
        super(message);
    }
}
