package com.ll.exam.final__2022_10_08.api.member.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class LoginRequest {
    @NotBlank(message = "아이디를 입력해주세요")
    private String username;
    @NotBlank(message = "비밀번호를 입력해주세요")
    private String password;
}
