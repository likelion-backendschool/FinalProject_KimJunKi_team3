package com.ll.finalproject.app.member.form;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
public class MemberJoinForm {

    @NotBlank(message = "아이디는 필수항목입니다.")
    private String username;

    @NotBlank(message = "비밀번호는 필수항목입니다.")
    private String password;

    @NotBlank(message = "비밀번호 확인은 필수항목입니다.")
    private String passwordConfirm;

    @NotBlank(message = "이메일은 필수항목입니다.")
    @Email(message = "올바른 이메일 형식으로 입력해주세요.")
    private String email;

}
