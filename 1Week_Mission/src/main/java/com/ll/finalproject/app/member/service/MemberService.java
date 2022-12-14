package com.ll.finalproject.app.member.service;

import com.ll.finalproject.app.member.entity.Member;
import com.ll.finalproject.app.member.exception.*;
import com.ll.finalproject.app.member.repository.MemberRepository;
import com.ll.finalproject.app.security.dto.MemberContext;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.bytebuddy.utility.RandomString;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final MailService mailService;

    public Member join(String username, String password, String email) {

        if (memberRepository.existsByUsername(username)) {
            throw new JoinUsernameDuplicatedException("이미 사용중인 아이디 입니다.");
        }

        if (memberRepository.existsByEmail(email)) {
            throw new JoinEmailDuplicatedException("이미 사용중인 이메일 입니다.");
        }

        Member member = Member.builder()
                .username(username)
                .password(passwordEncoder.encode(password))
                .email(email)
                .build();
        memberRepository.save(member);
        mailService.sendJoinMail(email);

        return member;
    }

    @Transactional(readOnly = true)
    public Optional<Member> findByUsername(String username) {
        return memberRepository.findByUsername(username);
    }

    @Transactional(readOnly = true)
    public Member findByEmail(String email) {
        return memberRepository.findByEmail(email).orElseThrow(
                () -> new MemberNotFoundException("존재하지 않는 이메일입니다."));
    }

    public void modify(String username, String email, String nickname) {
        Member member = memberRepository.findByUsername(username).orElseThrow(
                () -> new MemberNotFoundException("존재하지 않는 회원입니다."));

        if (StringUtils.hasText(nickname)) {
            member.changeEmailAndNickname(email, nickname);
        } else {
            member.changeEmail(email);
        }

        memberRepository.save(member);
        forceAuthentication(member); // 세션 갱신
    }

    public void modifyPassword(String username, String oldPassword, String newPassword) {
        Member member = memberRepository.findByUsername(username).orElseThrow(
                () -> new MemberNotFoundException("존재하지 않는 회원입니다."));

        if (!checkOldPassword(oldPassword, member.getPassword())) {
            throw new PasswordNotSameException("기존 비밀번호와 일치하지 않습니다.");
        }

        member.changePassword(passwordEncoder.encode(newPassword));
        memberRepository.save(member);
    }

    public boolean checkOldPassword(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }

    public void sendTempPasswordToEmail(String username, String email) {
        Member member = memberRepository.findByUsernameAndEmail(username, email).orElseThrow(
                () -> new MemberNotFoundException("일치하는 회원이 존재하지 않습니다."));

        // 10자리수의 임시 비밀번호 생성
        String tempPassword = new RandomString(10, new Random()).nextString();

        member.changePassword(passwordEncoder.encode(tempPassword));
        memberRepository.save(member);

        mailService.sendTempPasswordMail(member.getEmail(), tempPassword);
    }

    public Member beAuthor(Member member, String nickname) {
        Optional<Member> opMember = memberRepository.findByNickname(nickname);

        if (opMember.isPresent()) {
            throw new AlreadyExistsNicknameException("이미 존재하는 필명입니다.");
        }

        Member foundMember = memberRepository.findById(member.getId())
                .orElseThrow(() -> new MemberNotFoundException("존재하지 않는 유저입니다."));

        foundMember.changeNickname(nickname);
        forceAuthentication(foundMember);

        return member;
    }

    // 세션 갱신
    private void forceAuthentication(Member member) {
        MemberContext memberContext = new MemberContext(member, member.genAuthorities());

        UsernamePasswordAuthenticationToken authentication =
                UsernamePasswordAuthenticationToken.authenticated(
                        memberContext,
                        member.getPassword(),
                        memberContext.getAuthorities()
                );
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);
    }
}
