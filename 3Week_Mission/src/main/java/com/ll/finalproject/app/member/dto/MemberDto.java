package com.ll.finalproject.app.member.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MemberDto {
    private Long id;

    private String username;

    private String nickname;

    private String email;

    private long restCash;

    private LocalDateTime createDate;

    private LocalDateTime modifyDate;

    public boolean hasRestCash() {
        if (restCash == 0) {
            return false;
        }
        return true;
    }
}
