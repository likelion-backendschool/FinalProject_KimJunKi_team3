package com.ll.finalproject.app.member.exception;

public class JoinUsernameDuplicatedException extends RuntimeException{
    public JoinUsernameDuplicatedException(String message) {
        super(message);
    }
}
