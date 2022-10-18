package com.ll.finalproject.app.member.form;

import lombok.Data;

import javax.validation.constraints.NotBlank;
@Data
public class MemberModifyPasswordForm {

    @NotBlank(message = "비밀번호를 입력해주세요..")
    private String oldPassword;

    @NotBlank(message = "비밀번호를 입력해주세요..")
    private String newPassword;

    @NotBlank(message = "비밀번호 확인을 입력해주세요..")
    private String newPasswordConfirm;
}