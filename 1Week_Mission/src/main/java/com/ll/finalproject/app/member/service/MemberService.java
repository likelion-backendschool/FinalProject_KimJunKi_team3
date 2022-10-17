package com.ll.finalproject.app.member.service;

import com.ll.finalproject.app.member.entity.Member;
import com.ll.finalproject.app.member.exception.JoinEmailDuplicatedException;
import com.ll.finalproject.app.member.exception.JoinUsernameDuplicatedException;
import com.ll.finalproject.app.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    public Member join(String username, String password, String email, String nickname) {

        try {
            Member member = Member.builder()
                    .username(username)
                    .password(password)
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
}
