package com.ll.exam.final__2022_10_08.api.member.controller;

import com.ll.exam.final__2022_10_08.app.base.dto.RsData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@Slf4j
@RestControllerAdvice
public class ApiMemberControllerAdvice {

    @ExceptionHandler
    public ResponseEntity<RsData> handleValidationExceptions(MethodArgumentNotValidException ex) {
        log.error("[MethodArgumentNotValidException] ex", ex);
        return ResponseEntity.badRequest().body(
                RsData.of("F-MethodArgumentNotValidException", ex.getMessage()));
    }
}
