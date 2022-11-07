package com.ll.exam.final__2022_10_08.api.service;

import com.ll.exam.final__2022_10_08.api.common.exception.ExceptionType;
import com.ll.exam.final__2022_10_08.api.common.exception.MyBookInvalidException;
import com.ll.exam.final__2022_10_08.api.myBook.dto.MyBooksResponse;
import com.ll.exam.final__2022_10_08.app.member.entity.Member;
import com.ll.exam.final__2022_10_08.app.member.service.MemberService;
import com.ll.exam.final__2022_10_08.app.myBook.service.MyBookService;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.test.context.ActiveProfiles;

import org.springframework.transaction.annotation.Transactional;


import java.util.List;

import static org.assertj.core.api.Assertions.assertThatThrownBy;


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

    @Test
    @DisplayName("getMyBook 존재하지 않은 id일 경우 MyBookInvalidException 예외 ")
    void t2() {
        // When
        Member member = memberService.findByUsername("user1").get();

        // Then
        assertThatThrownBy(
                () -> myBookService.getMyBook(1000L, member.getId())
        ).isInstanceOf(MyBookInvalidException.class)
                .hasMessageContaining(ExceptionType.MEMBER_USERNAME_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("getMyBook 유저가 도서에 대한 권한이 없는 경우 ")
    void t3() {
        // Then
        assertThatThrownBy(
                () -> myBookService.getMyBook(1L, 1000L)
        ).isInstanceOf(MyBookInvalidException.class)
                .hasMessageContaining(ExceptionType.INVALID_MYBOOK_OWNER.getMessage());
    }
}
