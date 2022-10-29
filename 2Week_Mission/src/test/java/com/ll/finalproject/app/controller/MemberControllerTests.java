package com.ll.finalproject.app.controller;

import com.ll.finalproject.app.member.controller.MemberController;
import com.ll.finalproject.app.member.entity.Member;
import com.ll.finalproject.app.member.repository.MemberRepository;
import com.ll.finalproject.app.member.service.MemberService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@Transactional
@ActiveProfiles({"test", "base"})
@AutoConfigureMockMvc
class MemberControllerTests {

    @Autowired
    private MockMvc mvc;
    @Autowired
    private MemberService memberService;
    @Autowired
    private MemberRepository memberRepository;

    @Test
    @DisplayName("정상적인 회원가입")
    @WithAnonymousUser
    void t1() throws Exception {
        // WHEN
        ResultActions resultActions = mvc
                .perform(post("/member/join")
                        .with(csrf())
                        .param("username","user123")
                        .param("password","1234!")
                        .param("passwordConfirm","1234!")
                        .param("email","user123@naver.com")
                ).andDo(print());

        // THEN
        resultActions
                .andExpect(status().is3xxRedirection())
                .andExpect(handler().handlerType(MemberController.class))
                .andExpect(handler().methodName("join"))
                .andExpect(redirectedUrl("/"));

        assertThat(memberRepository.findByUsername("user123").isPresent()).isTrue();
    }

    @Test
    @DisplayName("이미 존재하는 아이디로 회원가입을 하는 경우 bindingResult error 존재")
    void t2() throws Exception {
        // WHEN
        ResultActions resultActions = mvc
                .perform(post("/member/join")
                        .with(csrf())
                        .param("username","user1")
                        .param("password","1234!")
                        .param("passwordConfirm","1234!")
                        .param("email","user123@naver.com")
                ).andDo(print());

        // THEN
        resultActions
                .andExpect(status().is2xxSuccessful())
                .andExpect(handler().handlerType(MemberController.class))
                .andExpect(handler().methodName("join"))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeHasFieldErrors("memberJoinForm", "username"));
    }

    @Test
    @DisplayName("user1의 프로필 페이지에 접속하면 이메일 정보가 나와있어야 한다.")
    @WithUserDetails("user1")
    void t3() throws Exception {
        // WHEN
        ResultActions resultActions = mvc
                .perform(get("/member/profile"))
                .andDo(print());

        // THEN
        resultActions
                .andExpect(status().is2xxSuccessful())
                .andExpect(handler().handlerType(MemberController.class))
                .andExpect(handler().methodName("showProfile"))
                .andExpect(content().string(containsString("user1@test.com")));
    }

    @Test
    @DisplayName("user1의 이메일 수정 폼")
    @WithUserDetails("user1")
    void t4() throws Exception {
        // WHEN
        ResultActions resultActions = mvc
                .perform(get("/member/modify"));

        // THEN
        resultActions
                .andExpect(status().is2xxSuccessful())
                .andExpect(handler().handlerType(MemberController.class))
                .andExpect(handler().methodName("showModify"))
                .andExpect(content().string(containsString("user1@test.com")));
    }

    @Test
    @DisplayName("user1의 이메일 변경.")
    @WithUserDetails("user1")
    void t5() throws Exception {
        // WHEN
        ResultActions resultActions = mvc
                .perform(post("/member/modify")
                        .param("email", "user123@test.com"))
                .andDo(print());

        // THEN
        resultActions
                .andExpect(status().is3xxRedirection())
                .andExpect(handler().handlerType(MemberController.class))
                .andExpect(handler().methodName("modify"));

        Member member = memberService.findByUsername("user1");
        assertThat(member.getEmail()).isEqualTo("user123@test.com");
    }

    @Test
    @DisplayName("user1의 비밀번호 수정 폼")
    @WithUserDetails("user1")
    void t6() throws Exception {
        // WHEN
        ResultActions resultActions = mvc
                .perform(get("/member/modifyPassword"));

        // THEN
        resultActions
                .andExpect(status().is2xxSuccessful())
                .andExpect(handler().handlerType(MemberController.class))
                .andExpect(handler().methodName("showModifyPassword"))
                .andExpect(content().string(containsString("비밀번호 변경")));
    }

    @Test
    @DisplayName("user1의 비밀번호 변경.")
    @WithUserDetails("user1")
    void t7() throws Exception {
        // WHEN
        ResultActions resultActions = mvc
                .perform(post("/member/modifyPassword")
                        .param("oldPassword", "1234!")
                        .param("newPassword", "123")
                        .param("newPasswordConfirm", "123"))
                .andDo(print());

        // THEN
        resultActions
                .andExpect(status().is3xxRedirection())
                .andExpect(handler().handlerType(MemberController.class))
                .andExpect(handler().methodName("modifyPassword"));

        Member member = memberService.findByUsername("user1");

        assertThat(memberService.checkOldPassword("123", member.getPassword())).isTrue();
    }

    @Test
    @DisplayName("user1의 비밀번호 변경, 기존 비밀번호가 틀렸을 경우 PasswordNotSameException 예외 발생.")
    @WithUserDetails("user1")
    void t8() throws Exception {
        ResultActions resultActions = mvc.perform(post("/member/modifyPassword")
                            .param("oldPassword", "123123")
                            .param("newPassword", "123")
                            .param("newPasswordConfirm", "123"));
        // THEN
        resultActions
                .andExpect(status().is2xxSuccessful())
                .andExpect(handler().handlerType(MemberController.class))
                .andExpect(handler().methodName("modifyPassword"))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeHasFieldErrors("memberModifyPasswordForm", "oldPassword"));
    }

    @Test
    @DisplayName("user1의 비밀번호 변경, 비밀번호와 비밀번호 확인이 틀릴 경우 BindingResult error 발생.")
    @WithUserDetails("user1")
    void t9() throws Exception {
        // WHEN
        ResultActions resultActions = mvc
                .perform(post("/member/modifyPassword")
                        .param("oldPassword", "1234!")
                        .param("newPassword", "123")
                        .param("newPasswordConfirm", "1234"));
        // THEN
        resultActions
                .andExpect(status().is2xxSuccessful())
                .andExpect(handler().handlerType(MemberController.class))
                .andExpect(handler().methodName("modifyPassword"))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeHasFieldErrors("memberModifyPasswordForm", "newPassword"));
    }

    @Test
    @DisplayName("user1의 아이디 찾기")
    void t10() throws Exception {
        // WHEN
        ResultActions resultActions = mvc
                .perform(post("/member/findUsername")
                        .param("email", "user1@test.com"))
                .andDo(print());

        // THEN
        resultActions
                .andExpect(status().is2xxSuccessful())
                .andExpect(handler().handlerType(MemberController.class))
                .andExpect(handler().methodName("findUsername"))
                .andExpect(content().string(containsString("user1")));
    }

    @Test
    @DisplayName("user1의 아이디 찾기, 존재하지 않은 이메일인 경우 BindingResult error 발생")
    void t11() throws Exception {
        // WHEN
        ResultActions resultActions = mvc
                .perform(post("/member/findUsername")
                        .param("email", "user123@test.com"));
        // THEN
        resultActions
                .andExpect(status().is2xxSuccessful())
                .andExpect(handler().handlerType(MemberController.class))
                .andExpect(handler().methodName("findUsername"))
                .andExpect(model().hasErrors())
                .andExpect(content().string(containsString("존재하지 않는 이메일입니다.")));
    }

    @Test
    @DisplayName("비밀번호 찾기")
    void t12() throws Exception {
        // WHEN
        ResultActions resultActions = mvc
                .perform(post("/member/findPassword")
                        .with(csrf())
                        .param("email", "user1@test.com")
                        .param("username", "user1")
                )
                .andDo(print());

        // THEN
        resultActions
                .andExpect(status().is2xxSuccessful())
                .andExpect(handler().handlerType(MemberController.class))
                .andExpect(handler().methodName("findPassword"))
                .andExpect(content().string(containsString("이메일이 전송되었습니다.")));
    }

    @Test
    @DisplayName("일반 권한을 가진 user1 회원을 작가로 등록")
    @WithUserDetails("user1")
    void t13() throws Exception {
        // WHEN
        ResultActions resultActions = mvc
                .perform(post("/member/beAuthor")
                        .with(csrf())
                        .param("nickname", "김작가")
                )
                .andDo(print());

        // THEN
        resultActions
                .andExpect(status().is3xxRedirection())
                .andExpect(handler().handlerType(MemberController.class))
                .andExpect(handler().methodName("beAuthor"))
                .andExpect(redirectedUrl("/member/profile"));
    }


}