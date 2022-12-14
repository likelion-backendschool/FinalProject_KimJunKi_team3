package com.ll.finalproject.app.controller;

import com.ll.finalproject.app.base.rq.Rq;
import com.ll.finalproject.app.member.entity.Member;
import com.ll.finalproject.app.member.service.MemberService;
import com.ll.finalproject.app.product.contoller.ProductController;
import com.ll.finalproject.app.product.service.ProductService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles({"test", "base"})
public class ProductControllerTests {

    @Autowired
    private MockMvc mvc;
    @Autowired
    private ProductService productService;
    @Autowired
    private MemberService memberService;
    @Autowired
    private Rq rq;


    @Test
    @DisplayName("도서 등록 폼")
    @WithMockUser(username = "user1", roles = {"MEMBER","AUTHOR"})
    void t1() throws Exception {

        // WHEN
        ResultActions resultActions = mvc
                .perform(get("/product/create"))
                .andDo(print());

        // THEN
        resultActions
                .andExpect(status().is2xxSuccessful())
                .andExpect(handler().handlerType(ProductController.class))
                .andExpect(handler().methodName("showCreate"))
                .andExpect(content().string(containsString("도서 등록")));
    }

    @Test
    @DisplayName("도서 등록")
    @WithUserDetails("user5")
    void t2() throws Exception {

        // WHEN
        ResultActions resultActions = mvc
                .perform(post("/product/create")
                        .with(csrf())
                        .param("subject", "상품명")
                        .param("price", "18000")
                        .param("description", "올해 가장 인기있는 책")
                        .param("postKeywordId", "5")
                )
                .andDo(print());

        // THEN
        resultActions
                .andExpect(status().is3xxRedirection())
                .andExpect(handler().handlerType(ProductController.class))
                .andExpect(handler().methodName("create"))
                .andExpect(redirectedUrlPattern("/product/**"));

//        Long productId = Long.valueOf(resultActions.andReturn().getResponse().getRedirectedUrl().replace("/product/", "").split("\\?", 2)[0]);
//        assertThat(productService.findById(productId).isPresent()).isTrue();
    }
}