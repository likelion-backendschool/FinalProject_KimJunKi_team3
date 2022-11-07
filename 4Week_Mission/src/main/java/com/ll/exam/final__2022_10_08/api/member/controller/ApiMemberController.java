package com.ll.exam.final__2022_10_08.api.member.controller;

import com.ll.exam.final__2022_10_08.api.member.dto.LoginRequest;
import com.ll.exam.final__2022_10_08.app.base.dto.RsData;
import com.ll.exam.final__2022_10_08.app.member.service.MemberService;
import com.ll.exam.final__2022_10_08.util.Ut;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import javax.validation.Valid;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/member")
public class ApiMemberController {

    private final MemberService memberService;

    @PostMapping("/login")
    public ResponseEntity login(@RequestBody @Valid LoginRequest loginRequest) {

        memberService.validateLogin(loginRequest.getUsername(), loginRequest.getPassword());

        String accessToken = "JWT_Access_Token";

        return ResponseEntity
                .ok()
                .headers(Ut.spring.httpHeadersOf("Authentication", accessToken))
                .body(RsData.of(
                        "S-1",
                        "로그인 성공, Access Token을 발급합니다.",
                        Ut.mapOf(
                                "accessToken", accessToken
                        )
                ));
    }
}
