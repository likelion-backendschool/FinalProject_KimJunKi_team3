package com.ll.finalproject.app.member;

import com.ll.finalproject.app.base.rq.Rq;
import com.ll.finalproject.app.member.entity.Member;
import com.ll.finalproject.app.member.exception.AlreadyExistsNicknameException;
import com.ll.finalproject.app.member.exception.JoinEmailDuplicatedException;
import com.ll.finalproject.app.member.exception.JoinUsernameDuplicatedException;
import com.ll.finalproject.app.member.exception.PasswordNotSameException;
import com.ll.finalproject.app.member.repository.MemberRepository;
import com.ll.finalproject.app.member.service.MemberService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;


@SpringBootTest
@Transactional
@ActiveProfiles({"test", "base"})
class MemberServiceTest {

    @Autowired
    private MemberService memberService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private Rq rq;

    @Test
    @DisplayName("정상적인 회원가입")
    void join1() {
        String username = "user123";
        String password = "1234!";
        String email = "user123@test.com";

        memberService.join(username, password, email);
        Member foundMember = memberService.findByUsername(username).get();

        assertThat(foundMember.getCreateDate()).isNotNull();
        assertThat(foundMember.getUsername()).isNotNull();

        assertThat(passwordEncoder.matches(password, foundMember.getPassword())).isTrue();
    }

    @Test
    @DisplayName("동일한 아이디로 회원가입을 하는 경우 JoinUsernameDuplicatedException 예외가 발생한다.")
    void join2() {
        assertThrows(JoinUsernameDuplicatedException.class, () -> {
            Member member = memberService.join("user1", "1234", "user123@naver.com");
        });
    }

    @Test
    @DisplayName("동일한 이메일로 회원가입을 하는 경우 JoinUsernameDuplicatedException 예외가 발생한다.")
    void join3() {
        assertThrows(JoinEmailDuplicatedException.class, () -> {
            Member member = memberService.join("user123", "1234", "user1@test.com");
        });
    }

    @Test
    @DisplayName("회원 정보 수정, nickname 없는 경우")
    void modify1() {
        String username = "user1";
        String email = "user123@test.com";
        String nickname = null;

        Member oldMember = memberService.findByUsername(username).get();
        assertThat(oldMember.getEmail()).isNotEqualTo(email);

        memberService.modify(username, email, nickname);
        Member newMember = memberService.findByUsername(username).get();

        assertThat(newMember.getEmail()).isEqualTo(email);
        assertThat(nickname).isNull();
    }

    @Test
    @DisplayName("회원 정보 수정, nickname 있는 경우")
    void modify2() {
        String username = "user1";
        String email = "user123@test.com";
        String nickname = "김작가";

        Member oldMember = memberService.findByUsername(username).get();
        assertThat(oldMember.getEmail()).isNotEqualTo(email);

        memberService.modify(username, email, nickname);
        Member newMember = memberService.findByUsername(username).get();

        assertThat(newMember.getEmail()).isEqualTo(email);
        assertThat(newMember.getNickname()).isEqualTo(nickname);
    }

    @Test
    @DisplayName("회원 비밀번호 수정")
    void modifyPassword1() {
        String username = "user1";
        String oldPassword = "1234!";
        String newPassword = "123123";

        Member oldMember = memberService.findByUsername(username).get();

        memberService.modifyPassword(username, oldPassword, newPassword);

        Member newMember = memberService.findByUsername(username).get();

        boolean checkPwd = memberService.checkOldPassword(newPassword, newMember.getPassword());
        assertThat(checkPwd).isTrue();
    }
    @Test
    @DisplayName("회원 비밀번호 수정, 기존 비번이 틀린 경우 PasswordNotSameException 예외 발생")
    void modifyPassword2() {
        String username = "user1";
        String oldPassword = "12345!";
        String newPassword = "123123";

        assertThrows(PasswordNotSameException.class, () -> {
            memberService.modifyPassword(username, oldPassword, newPassword);
        });
    }

    @Test
    @DisplayName("작가 되기, AUTHOR 권한이 새로 생김")
    void beAuthor1() {
        String nickname = "김작가";

        Member member = memberService.findByUsername("user1").get();
        List<GrantedAuthority> oldGrantedAuthorities = member.genAuthorities();

        memberService.beAuthor(member, nickname);

        Member newMember = memberService.findByUsername(member.getUsername()).get();

        assertThat(newMember.getNickname()).isEqualTo(nickname);
        assertThat(member.genAuthorities()).isNotEqualTo(oldGrantedAuthorities);
    }

    @Test
    @DisplayName("작가 되기, 이미 존재하는 필명일 경우 AlreadyExistsNicknameException 예외 발생")
    @WithUserDetails("user1")
    void beAuthor2() {
        String nickname = "김작가";
        Member member = rq.getMember();

        Member member1 = memberService.findByUsername("user2").get();
        memberService.beAuthor(member1, nickname);

        assertThrows(AlreadyExistsNicknameException.class, () -> {
            memberService.beAuthor(member, nickname);
        });
    }
}