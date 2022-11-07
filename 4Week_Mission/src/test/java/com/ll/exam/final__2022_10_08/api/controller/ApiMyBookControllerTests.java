package com.ll.exam.final__2022_10_08.api.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
public class ApiMyBookControllerTests {

    @Autowired
    private MockMvc mvc;

    @Test
    @DisplayName("로그인 없이 GET/api/v1/myBooks 요청 보낼 경우, 403 ")
    void t1() throws Exception {
        // When
        ResultActions resultActions = mvc
                .perform(
                        get("/api/v1/myBooks/"))
                .andDo(print());

        // Then
        resultActions
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("user1이 GET/api/v1/myBooks 요청 보낼 경우 도서 데이터 반환")
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

        String accessToken = response.getHeader("Authentication");

        // When
        resultActions = mvc
                .perform(
                        get("/api/v1/myBooks/")
                                .header("Authorization", "Bearer " + accessToken))
                .andDo(print());

        // Then
        resultActions
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.resultCode").value("S-1"))
                .andExpect(jsonPath("$.msg").value("성공"))
                .andExpect(jsonPath("$..data[?(@.ownerId == '%s')]",1).exists())
                .andExpect(jsonPath("$..product[?(@.authorName == '%s')]","user1").exists())
                .andExpect(jsonPath("$..product[?(@.authorName == '%s')]","홍길순").exists())
                .andExpect(jsonPath("$..product[?(@.subject == '%s')]","상품명2").exists())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.fail").value(false));
    }

    @Test
    @DisplayName("GET/api/v1/myBooks{myBookId} 요청 보낼 경우, 도서 데이터 반환 ")
    @WithUserDetails("user1")
    void t3() throws Exception {
        // When
        ResultActions resultActions = mvc
                .perform(
                        get("/api/v1/myBooks/"))
                .andDo(print());

        // Then
        resultActions
                .andExpect(status().isForbidden());
    }
}
