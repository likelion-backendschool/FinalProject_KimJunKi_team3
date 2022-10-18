package com.ll.finalproject.app.member.service;

import com.ll.finalproject.app.member.entity.Member;
import com.ll.finalproject.app.member.exception.JoinEmailDuplicatedException;
import com.ll.finalproject.app.member.exception.JoinUsernameDuplicatedException;
import com.ll.finalproject.app.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    private final PasswordEncoder passwordEncoder;

    public Member join(String username, String password, String email, String nickname) {

        try {
            Member member = Member.builder()
                    .username(username)
                    .password(passwordEncoder.encode(password))
                    .email(email)
                    .nickname(nickname)
                    .build();
            memberRepository.save(member);

            return member;

        } catch (DataIntegrityViolationException e) {
            if (memberRepository.existsByUsername(username)) {
                throw new JoinUsernameDuplicatedException("이미 사용중인 아이디 입니다.");
            } else {
                throw new JoinEmailDuplicatedException("이미 사용중인 이메일 입니다.");
            }
        }
    }

    @Transactional(readOnly = true)
    public Optional<Member> findByUsername(String username) {
        return memberRepository.findByUsername(username);
    }

    public void modify(Member member, String email, String nickname) {
        member.changeEmailAndNickname(email, nickname);
        memberRepository.save(member);
    }
}
