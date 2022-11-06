package com.ll.exam.final__2022_10_08.api.member.controller;

import com.ll.exam.final__2022_10_08.api.member.dto.LoginRequest;
import com.ll.exam.final__2022_10_08.app.base.dto.RsData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/member")
public class ApiMemberController {

    @PostMapping("/login")
    public String login(@RequestBody @Valid LoginRequest memberLoginRequest, HttpServletResponse response) {
        response.addHeader("Authentication", "JWT토큰");
        return "";
    }
}
