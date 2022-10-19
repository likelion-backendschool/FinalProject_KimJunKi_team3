package com.ll.finalproject.app.base.initData;

import com.ll.finalproject.app.member.entity.Member;
import com.ll.finalproject.app.member.service.MemberService;
import com.ll.finalproject.app.post.keyword.entity.PostKeyword;
import com.ll.finalproject.app.post.keyword.service.PostKeywordService;
import com.ll.finalproject.app.post.service.PostService;
import com.ll.finalproject.app.product.entity.Product;
import com.ll.finalproject.app.product.service.ProductService;

import java.util.Optional;

public interface InitDataBefore {
    default void before(MemberService memberService, PostService postService, ProductService productService, PostKeywordService postKeywordService) {
        Member member1 = memberService.join("user1", "1234!","user1@test.com",null);
        Member member2 = memberService.join("user2", "1234!","user2@test.com",null);

        Member member3 = memberService.join("user3", "1234!","user3@test.com","유저3");
        Member member4 = memberService.join("user4", "1234!","user4@test.com","유저4");

        for (int i = 0; i < 10; i++) {
            postService.write(member1, "제목 " + i, "내용 " + i,"#그리움 #행복");

        }
        for (int i = 0; i < 100; i++) {
            postService.write(member1, "제목 " + i, "내용 " + i,null);
        }

        PostKeyword keyword1 = postKeywordService.findByContent("그리움").get();
        PostKeyword keyword2 = postKeywordService.findByContent("행복").get();

        Product product1 = productService.create(member1, keyword1, "그리움 도서", 12000);
        Product product2 = productService.create(member1, keyword2, "행복 도서", 22000);
    }
}