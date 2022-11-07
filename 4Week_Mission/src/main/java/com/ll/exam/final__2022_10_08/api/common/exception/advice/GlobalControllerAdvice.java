package com.ll.exam.final__2022_10_08.api.common.exception.advice;

import com.ll.exam.final__2022_10_08.api.common.exception.ExceptionType;
import com.ll.exam.final__2022_10_08.app.base.dto.RsData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;


@Slf4j
@RestControllerAdvice
public class GlobalControllerAdvice {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<RsData> handleValidationExceptions(MethodArgumentNotValidException ex) {
        log.error("[MethodArgumentNotValidException] ex", ex);
        String msg = ex
                .getBindingResult()
                .getAllErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.joining("/"));

        String data = ex
                .getBindingResult()
                .getAllErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getCode)
                .collect(Collectors.joining("/"));


        return ResponseEntity.badRequest().body(
                RsData.of("F-MethodArgumentNotValidException", msg, data));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<RsData> handleValidationExceptions(HttpMessageNotReadableException ex) {
        log.error("[HttpMessageNotReadableException] ex", ex);

        return ResponseEntity.badRequest().body(
                RsData.of("F-HttpMessageNotReadableException", ExceptionType.UNHANDLED_EXCEPTION.getMessage()));
    }
}
