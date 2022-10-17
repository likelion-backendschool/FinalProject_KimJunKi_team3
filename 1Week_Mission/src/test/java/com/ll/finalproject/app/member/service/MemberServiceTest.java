package com.ll.finalproject.app.member.service;

import com.ll.finalproject.app.member.entity.Member;
import com.ll.finalproject.app.member.exception.JoinEmailDuplicatedException;
import com.ll.finalproject.app.member.exception.JoinUsernameDuplicatedException;
import com.ll.finalproject.app.member.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;


@SpringBootTest
@Transactional
@ActiveProfiles("test")
class MemberServiceTest {

    @Autowired
    private MemberService memberService;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    @DisplayName("정상적인 회원가입")
    void t1() {
        String username = "user123";
        String password = "1234!";
        String email = "user123@test.com";
        String nickname = "유저123";

        memberService.join(username, password, email, nickname);
        Member foundMember = memberService.findByUsername(username).get();

        assertThat(foundMember.getCreateDate()).isNotNull();
        assertThat(foundMember.getUsername()).isNotNull();
        System.out.println(foundMember.getPassword());
        System.out.println(password);
        assertThat(passwordEncoder.matches(password, foundMember.getPassword())).isTrue();
    }

    @Test
    @DisplayName("동일한 닉네임으로 회원가입을 하는 경우 JoinUsernameDuplicatedException 예외가 발생한다.")
    void t2() {
        assertThrows(JoinUsernameDuplicatedException.class, () -> {
            Member member = memberService.join("user1", "1234", "user123@naver.com", null);
        });
    }

    @Test
    @DisplayName("동일한 이메일로 회원가입을 하는 경우 JoinUsernameDuplicatedException 예외가 발생한다.")
    void t3() {
        assertThrows(JoinEmailDuplicatedException.class, () -> {
            Member member = memberService.join("user123", "1234", "user1@test.com", null);
        });
    }
}