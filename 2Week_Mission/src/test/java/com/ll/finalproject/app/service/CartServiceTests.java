package com.ll.finalproject.app.service;

import com.ll.finalproject.app.product.cart.entity.CartItem;
import com.ll.finalproject.app.product.cart.service.CartService;
import com.ll.finalproject.app.member.entity.Member;
import com.ll.finalproject.app.member.repository.MemberRepository;
import com.ll.finalproject.app.product.entity.Product;
import com.ll.finalproject.app.product.service.ProductService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
//@Transactional
@ActiveProfiles({"test", "base"})
public class CartServiceTests {

    @Autowired
    private ProductService productService;

    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private CartService cartService;

    @Test
    @DisplayName("장바구니에 담기")
    void t1() {

        Member buyer = memberRepository.findByUsername("user3").get();

        Product product1 = productService.getProduct(1L);
        Product product2 = productService.getProduct(2L);

        CartItem cartItem1 = cartService.addItem(buyer.getId(), product1);
        CartItem cartItem2 = cartService.addItem(buyer.getId(), product2);

        assertThat(cartItem1).isNotNull();
        assertThat(cartItem2).isNotNull();
    }

    @Test
    @DisplayName("장바구니에서 제거")
    void t2() {
        Member buyer1 = memberRepository.findByUsername("user3").get();
        Member buyer2 = memberRepository.findByUsername("user4").get();

        Product product1 = productService.getProduct(1L);
        Product product2 = productService.getProduct(2L);
        Product product3 = productService.getProduct(3L);
        Product product4 = productService.getProduct(4L);

        cartService.removeItem(buyer1, product1);
        cartService.removeItem(buyer1, product2);
        cartService.removeItem(buyer2, product3);
        cartService.removeItem(buyer2, product4);

        assertThat(cartService.hasItem(buyer1, product1)).isFalse();
        assertThat(cartService.hasItem(buyer1, product1)).isFalse();
        assertThat(cartService.hasItem(buyer1, product1)).isFalse();
        assertThat(cartService.hasItem(buyer1, product1)).isFalse();
    }
}