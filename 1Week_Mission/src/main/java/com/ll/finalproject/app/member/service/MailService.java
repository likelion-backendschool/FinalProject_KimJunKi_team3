package com.ll.finalproject.app.member.service;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender javaMailSender;

    @Async
    public void sendJoinMail(String email) {

        // SimpleMailMessage (단순 텍스트 구성 메일 메시지 생성할 때 이용)
        SimpleMailMessage simpleMessage = new SimpleMailMessage();

        // 수신자 설정
        simpleMessage.setTo(email);

        // 메일 제목
        simpleMessage.setSubject("가입을 축하드립니다.");

        // 메일 내용
        simpleMessage.setText("저희 웹 사이트에 가입해 주셔서 감사합니다. 이제 가족이 되었으니, 유용한 링크를 보내드릴게요.");

        // 메일 발송
        javaMailSender.send(simpleMessage);
    }
    @Async
    public void sendTempPasswordMail(String email, String tempPassword) {

        // SimpleMailMessage (단순 텍스트 구성 메일 메시지 생성할 때 이용)
        SimpleMailMessage simpleMessage = new SimpleMailMessage();

        // 수신자 설정
        simpleMessage.setTo(email);

        // 메일 제목
        simpleMessage.setSubject("final 프로젝트 임시 비밀번호입니다. ");

        // 메일 내용
        simpleMessage.setText("\""+tempPassword+"\" 입니다. \n 비밀번호를 변경해주세요.");

        // 메일 발송
        javaMailSender.send(simpleMessage);
    }
}