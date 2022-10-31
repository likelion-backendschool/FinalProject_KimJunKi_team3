package com.ll.finalproject.app.member.exception;

public class AlreadyExistsNicknameException extends RuntimeException{
    public AlreadyExistsNicknameException(String message) {
        super(message);
    }
}
