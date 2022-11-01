package com.ll.finalproject.app.controller;

import com.ll.finalproject.app.cart.controller.CartController;
import com.ll.finalproject.app.cart.entity.CartItem;
import com.ll.finalproject.app.cart.service.CartService;
import com.ll.finalproject.app.member.entity.Member;
import com.ll.finalproject.app.member.service.MemberService;
import com.ll.finalproject.app.product.service.ProductService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@Transactional
@ActiveProfiles({"test", "base"})
@AutoConfigureMockMvc
public class CartControllerTests {

    @Autowired
    private MockMvc mvc;
    @Autowired
    private MemberService memberService;
    @Autowired
    private CartService cartService;
    @Autowired
    private ProductService productService;

    @Test
    @DisplayName("도서 장바구니에 추가")
    @WithUserDetails("user4")
    void t2() throws Exception{
        // WHEN
        Member user4 = memberService.findByUsername("user4");
        List<CartItem> itemsByBuyer1 = cartService.getCartItemsByBuyer(user4.getId());

        ResultActions resultActions = mvc
                .perform(post("/cart/add/1")
                        .with(csrf())
                ).andDo(print());

        // THEN
        resultActions
                .andExpect(status().is3xxRedirection())
                .andExpect(handler().handlerType(CartController.class))
                .andExpect(handler().methodName("addItem"));

        List<CartItem> itemsByBuyer2 = cartService.getCartItemsByBuyer(user4.getId());
        assertThat(itemsByBuyer2.size()).isEqualTo( itemsByBuyer1.size() + 1);
    }

    @Test
    @DisplayName("도서 장바구니에 이미 추가된 도서 추가")
    @WithUserDetails("user2")
    void t3() throws Exception{
        // WHEN
        Member user4 = memberService.findByUsername("user2");
        List<CartItem> itemsByBuyer1 = cartService.getCartItemsByBuyer(user4.getId());

        ResultActions resultActions = mvc
                .perform(post("/cart/add/1")
                        .with(csrf())
                ).andDo(print());

        // THEN
        resultActions
                .andExpect(status().is3xxRedirection())
                .andExpect(handler().handlerType(CartController.class))
                .andExpect(handler().methodName("addItem"));

        List<CartItem> itemsByBuyer2 = cartService.getCartItemsByBuyer(user4.getId());
        assertThat(itemsByBuyer2.size()).isEqualTo( itemsByBuyer1.size() );
    }
}
