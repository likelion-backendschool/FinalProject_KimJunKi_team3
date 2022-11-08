package com.ll.exam.final__2022_10_08.api.member.controller;

import com.ll.exam.final__2022_10_08.api.member.dto.LoginRequest;
import com.ll.exam.final__2022_10_08.app.base.dto.RsData;
import com.ll.exam.final__2022_10_08.app.member.service.MemberService;
import com.ll.exam.final__2022_10_08.app.security.dto.MemberContext;
import com.ll.exam.final__2022_10_08.util.Ut;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


import javax.validation.Valid;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/member")
public class ApiMemberController {

    private final MemberService memberService;

    @PostMapping("/login")
    public ResponseEntity<RsData> login(@RequestBody @Valid LoginRequest loginRequest) {

        String accessToken = memberService.login(loginRequest.getUsername(), loginRequest.getPassword());

        return ResponseEntity.ok()
                .headers(Ut.spring.httpHeadersOf("Authentication", accessToken))
                .body(RsData.of(
                        "S-1",
                        "로그인 성공, Access Token을 발급합니다.",
                        Ut.mapOf(
                                "accessToken", accessToken
                        )
                ));
    }
    @Operation(summary =  "로그인된 회원이 보유한 도서 목록", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/me")
    public ResponseEntity<RsData> me(@AuthenticationPrincipal MemberContext memberContext) {

        return ResponseEntity
                .ok()
                .body(RsData.of(
                        "S-1",
                        "로그인 성공, Access Token을 발급합니다.",
                        memberContext
                ));
    }
}
