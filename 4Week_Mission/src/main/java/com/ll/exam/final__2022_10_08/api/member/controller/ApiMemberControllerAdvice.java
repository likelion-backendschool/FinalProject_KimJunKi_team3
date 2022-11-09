package com.ll.exam.final__2022_10_08.api.member.controller;

import com.ll.exam.final__2022_10_08.api.common.exception.MemberInvalidException;
import com.ll.exam.final__2022_10_08.app.base.dto.RsData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class ApiMemberControllerAdvice {

    @ExceptionHandler(MemberInvalidException.class)
    public ResponseEntity<RsData> MemberInvalidExceptions(MemberInvalidException ex) {
        log.error("[MemberInvalidException] ex", ex);

        return ResponseEntity
                .status(ex.getHttpStatus())
                .body(RsData.of(String.valueOf(ex.getCode()), ex.getMessage()));
    }
}