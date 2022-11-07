package com.ll.exam.final__2022_10_08.api.service;

import com.ll.exam.final__2022_10_08.api.myBook.dto.MyBooksResponse;
import com.ll.exam.final__2022_10_08.app.member.entity.Member;
import com.ll.exam.final__2022_10_08.app.member.service.MemberService;
import com.ll.exam.final__2022_10_08.app.myBook.entity.MyBook;
import com.ll.exam.final__2022_10_08.app.myBook.service.MyBookService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.test.context.ActiveProfiles;

import org.springframework.transaction.annotation.Transactional;


import java.util.List;


@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
public class ApiMyBookServiceTests {

    @Autowired
    private MyBookService myBookService;
    @Autowired
    private MemberService memberService;

    @Test
    @DisplayName("getMyBooks")
    void t1() {
        // When
        Member member = memberService.findByUsername("user1").get();
        List<MyBooksResponse> myBooks = myBookService.getMyBooks(member.getId());

        // Then
        System.out.println("myBooks = " + myBooks);
    }

}
