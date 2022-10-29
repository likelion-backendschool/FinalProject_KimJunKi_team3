package com.ll.finalproject.app.member.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class MemberBeAuthorForm {
    @NotBlank(message = "작가명을 입력해주세요.")
    private String nickname;
}
