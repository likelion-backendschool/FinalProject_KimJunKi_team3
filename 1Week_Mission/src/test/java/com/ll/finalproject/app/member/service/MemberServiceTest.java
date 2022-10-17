package com.ll.finalproject.app.member.service;

import com.ll.finalproject.app.member.entity.Member;
import com.ll.finalproject.app.member.exception.JoinEmailDuplicatedException;
import com.ll.finalproject.app.member.exception.JoinUsernameDuplicatedException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertThrows;


@SpringBootTest
class MemberServiceTest {

    @Autowired
    private MemberService memberService;

    @Test
    @DisplayName("동일한 닉네임으로 회원가입을 하는 경우 JoinUsernameDuplicatedException 예외가 발생한다.")
    void t1() {
        assertThrows(JoinUsernameDuplicatedException.class, () -> {
            Member member = memberService.join("user1", "1234", "user123@naver.com", null);
        });
    }

    @Test
    @DisplayName("동일한 이메일로 회원가입을 하는 경우 JoinUsernameDuplicatedException 예외가 발생한다.")
    void t2() {
        assertThrows(JoinEmailDuplicatedException.class, () -> {
            Member member = memberService.join("user123", "1234", "user1@test.com", null);
        });
    }
}