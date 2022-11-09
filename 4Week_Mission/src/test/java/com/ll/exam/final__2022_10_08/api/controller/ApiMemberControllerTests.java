package com.ll.exam.final__2022_10_08.api.controller;

import com.ll.exam.final__2022_10_08.app.member.service.MemberService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
public class ApiMemberControllerTests {

    private final String EXPRESSION_RESULTCODE = "$.[?(@.resultCode == '%s')]";
    private final String EXPRESSION_ACCESSTOKEN = "$..data[?(@.accessToken)]";
    @Autowired
    private MockMvc mvc;
    @Autowired
    private MemberService memberService;
    @BeforeEach
    void beforeEach() {
        memberService.deleteCacheKeyMember();
    }
    @Test
    @DisplayName("POST /api/v1/member/login 은 로그인 처리 URL 이다.")
    void t1() throws Exception {
        // When
        ResultActions resultActions = mvc
                .perform(
                        post("/api/v1/member/login")
                                .content("""
                                        {
                                            "username": "user1",
                                            "password": "1234"
                                        }
                                        """.stripIndent())
                                .contentType(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8))
                )
                .andDo(print());

        // Then
        resultActions
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    @DisplayName("POST /api/v1/member/login  으로 올바른 username과 password 데이터를 넘기면 JWT키를 발급해준다.")
    void t2() throws Exception {
        // When
        ResultActions resultActions = mvc
                .perform(
                        post("/api/v1/member/login")
                                .content("""
                                        {
                                            "username": "user1",
                                            "password": "1234"
                                        }
                                        """.stripIndent())
                                .contentType(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8))
                )
                .andDo(print());

        // Then
        resultActions
                .andExpect(status().is2xxSuccessful());

        MvcResult mvcResult = resultActions.andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();

        String authentication = response.getHeader("Authentication");

        assertThat(authentication).isNotEmpty();
    }

    @Test
    @DisplayName("POST /api/v1/member/login  username가 NotBlank일 경우 테스트 resultCode=F-MethodArgumentNotValidException 이다")
    void t3() throws Exception {
        // When
        ResultActions resultActions = mvc
                .perform(
                        post("/api/v1/member/login")
                                .content("""
                                        {
                                            "username": "",
                                            "password": "1234"
                                        }
                                        """.stripIndent())
                                .contentType(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8))
                )
                .andDo(print());

        // Then
        resultActions
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath(EXPRESSION_RESULTCODE, "F-MethodArgumentNotValidException").exists());
    }

    @Test
    @DisplayName("POST /api/v1/member/login LoginRequest 객체 형식과 다른 값이 들어올 경우 resultCode=F-HttpMessageNotReadableException")
    void t4() throws Exception {
        // When
        ResultActions resultActions = mvc
                .perform(
                        post("/api/v1/member/login")
                                .content("""
                                        {
                                            "nickname": "안녕",
                                            "password": "1234"
                                        }
                                        """.stripIndent())
                                .contentType(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8))
                )
                .andDo(print());

        // Then
        String expression = "$.[?(@.resultCode == '%s')]";
        resultActions
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath(expression, "F-HttpMessageNotReadableException").exists());
    }
    @Test
    @DisplayName("POST /api/v1/member/login  username 이 틀릴 경우 404 resultCode=101 ")
    void t5() throws Exception {
        // When
        ResultActions resultActions = mvc
                .perform(
                        post("/api/v1/member/login")
                                .content("""
                                        {
                                            "username": "usrse",
                                            "password": "1234"
                                        }
                                        """.stripIndent())
                                .contentType(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8))
                )
                .andDo(print());

        // Then
        resultActions
                .andExpect(status().isNotFound())
                .andExpect(jsonPath(EXPRESSION_RESULTCODE, "1001").exists());
    }
    @Test
    @DisplayName("POST /api/v1/member/login  password가 틀릴 경우 400 resultCode=102 ")
    void t6() throws Exception {
        // When
        ResultActions resultActions = mvc
                .perform(
                        post("/api/v1/member/login")
                                .content("""
                                        {
                                            "username": "user1",
                                            "password": "123456"
                                        }
                                        """.stripIndent())
                                .contentType(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8))
                )
                .andDo(print());

        // Then
        resultActions
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath(EXPRESSION_RESULTCODE, "1002").exists());
    }

    @Test
    @DisplayName("POST /api/v1/member/login 정상 로그인시에 토큰이 발급된다..")
    void t7() throws Exception {
        // When
        ResultActions resultActions = mvc
                .perform(
                        post("/api/v1/member/login")
                                .content("""
                                        {
                                            "username": "user1",
                                            "password": "1234"
                                        }
                                        """.stripIndent())
                                .contentType(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8))
                )
                .andDo(print());

        // Then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath(EXPRESSION_ACCESSTOKEN).exists());
    }
    @Test
    @DisplayName("로그인 후 얻은 JWT 토큰으로 현재 로그인 한 회원의 정보를 얻을 수 있다.")
    void t8() throws Exception {

        // When
        ResultActions resultActions = mvc
                .perform(
                        post("/api/v1/member/login")
                                .content("""
                                        {
                                            "username": "user1",
                                            "password": "1234"
                                        }
                                        """.stripIndent())
                                .contentType(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8))
                )
                .andDo(print());

        // Then
        resultActions
                .andExpect(status().is2xxSuccessful());

        MvcResult mvcResult = resultActions.andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();

        String accessToken = response.getHeader("Authentication");

        resultActions = mvc
                .perform(
                        get("/api/v1/member/me")
                                .header("Authorization", "Bearer " + accessToken)
                )
                .andDo(print());

        // Then
        resultActions
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.resultCode").value("S-1"))
                .andExpect(jsonPath("$.msg").value("성공"))
                .andExpect(jsonPath("$..member.id").value(1))
                .andExpect(jsonPath("$..member.createDate").isNotEmpty())
                .andExpect(jsonPath("$..member.modifyDate").isNotEmpty())
                .andExpect(jsonPath("$..member.username").value("user1"))
                .andExpect(jsonPath("$..member.email").value("user1@test.com"))
                .andExpect(jsonPath("$..member.emailVerified").value(false))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.fail").value(false));
    }

    @Test
    @DisplayName("POST /api/v1/member/login 로그인을 여러번 해도 같은 토큰 값을 받는다...")
    void t9() throws Exception {
        // When
        ResultActions resultActions = mvc
                .perform(
                        post("/api/v1/member/login")
                                .content("""
                                        {
                                            "username": "user1",
                                            "password": "1234"
                                        }
                                        """.stripIndent())
                                .contentType(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8))
                )
                .andDo(print());

        // Then
        resultActions
                .andExpect(status().is2xxSuccessful());

        String accessToken = resultActions.andReturn().getResponse().getHeader("Authentication");

        // When
        ResultActions resultActions2 = mvc
                .perform(
                        post("/api/v1/member/login")
                                .content("""
                                        {
                                            "username": "user1",
                                            "password": "1234"
                                        }
                                        """.stripIndent())
                                .contentType(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8))
                )
                .andDo(print());

        // Then
        resultActions2
                .andExpect(status().is2xxSuccessful());

        String accessToken2 = resultActions2.andReturn().getResponse().getHeader("Authentication");

        assertThat(accessToken2).isEqualTo(accessToken2);

    }
}
