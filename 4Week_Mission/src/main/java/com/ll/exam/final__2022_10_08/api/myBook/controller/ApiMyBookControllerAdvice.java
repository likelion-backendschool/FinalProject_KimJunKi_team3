package com.ll.exam.final__2022_10_08.api.myBook.controller;

import com.ll.exam.final__2022_10_08.api.common.exception.MemberInvalidException;
import com.ll.exam.final__2022_10_08.api.common.exception.MyBookInvalidException;
import com.ll.exam.final__2022_10_08.app.base.dto.RsData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class ApiMyBookControllerAdvice {

    @ExceptionHandler(MyBookInvalidException.class)
    public ResponseEntity<RsData> MyBookInvalidExceptions(MyBookInvalidException ex) {
        log.error("[MemberInvalidException] ex", ex);

        return ResponseEntity
                .status(ex.getHttpStatus())
                .body(RsData.of(String.valueOf(ex.getCode()), ex.getMessage()));
    }
}