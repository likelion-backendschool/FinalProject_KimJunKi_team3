package com.ll.finalproject.app.post.exception;

public class NoAuthorizationException extends RuntimeException{
    public NoAuthorizationException(String message) {
        super(message);
    }
}
