package com.ll.finalproject.app.base.initData;

import com.ll.finalproject.app.cart.service.CartService;
import com.ll.finalproject.app.member.service.MemberService;
import com.ll.finalproject.app.order.service.OrderService;
import com.ll.finalproject.app.post.keyword.service.PostKeywordService;
import com.ll.finalproject.app.post.service.PostService;
import com.ll.finalproject.app.product.service.ProductService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("dev")
public class DevInitData implements InitDataBefore {
    @Bean
    CommandLineRunner initData(
            MemberService memberService,
            PostService postService,
            ProductService productService,
            PostKeywordService postKeywordService,
            CartService cartService,
            OrderService orderService) {
        return args -> {
            before(memberService, postService, productService, postKeywordService, cartService, orderService);
        };
    }
}