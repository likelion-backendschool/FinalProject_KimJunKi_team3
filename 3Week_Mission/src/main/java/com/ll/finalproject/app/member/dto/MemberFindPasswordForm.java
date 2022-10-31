package com.ll.finalproject.app.member.dto;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
public class MemberFindPasswordForm {
    @NotBlank(message = "아이디는 필수항목입니다.")
    private String username;

    @NotBlank(message = "이메일은 필수항목입니다.")
    @Email(message = "올바른 이메일 형식으로 입력해주세요.")
    private String email;
}
