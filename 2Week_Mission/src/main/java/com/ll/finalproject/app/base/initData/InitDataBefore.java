package com.ll.finalproject.app.base.initData;

import com.ll.finalproject.app.cart.entity.CartItem;
import com.ll.finalproject.app.cart.service.CartService;
import com.ll.finalproject.app.member.entity.Member;
import com.ll.finalproject.app.member.service.MemberService;
import com.ll.finalproject.app.post.keyword.entity.PostKeyword;
import com.ll.finalproject.app.post.keyword.service.PostKeywordService;
import com.ll.finalproject.app.post.service.PostService;
import com.ll.finalproject.app.product.entity.Product;
import com.ll.finalproject.app.product.service.ProductService;

import java.util.Optional;

public interface InitDataBefore {
    default void before(MemberService memberService, PostService postService, ProductService productService,
                        PostKeywordService postKeywordService, CartService cartService) {

        /**
         * user1은 #그리움 #행복 #슬픔 #역경 태그가 달린 글 작성
         * user1은 #그리움 #행복 #슬픔 #역경 각각 태그에 대한 도서 등록
         * user3은 user1의 도서1, 도서2를 장바구니에 추가
         * user4은 user1의 도서3, 도서4를 장바구니에 추가
         */

        Member member1 = memberService.join("user1", "1234!","user1@test.com");
        Member member2 = memberService.join("user2", "1234!","user2@test.com");

        Member member3 = memberService.join("user3", "1234!","user3@test.com");
        Member member4 = memberService.join("user4", "1234!","user4@test.com");

        Member member5 = memberService.join("user5", "1234!","user5@test.com");
        memberService.beAuthor(member5, "김작가");
        postService.write(member1, "김작가 책", "내용", "#그리움");

        for (int i = 0; i < 10; i++) {
            postService.write(member1, "제목 " + i, "내용 " + i,"#그리움 #행복 #슬픔 #역경");

        }
        for (int i = 0; i < 10; i++) {
            postService.write(member1, "제목 " + i, "내용 " + i,null);
        }

        PostKeyword keyword1 = postKeywordService.findByContent("그리움").get();
        PostKeyword keyword2 = postKeywordService.findByContent("행복").get();
        PostKeyword keyword3 = postKeywordService.findByContent("슬픔").get();
        PostKeyword keyword4 = postKeywordService.findByContent("역경").get();

        Product product1 = productService.create(member1, keyword1, "그리움 도서","임시 책내용", "그리움 도서에 대한 설명", 12000);
        Product product2 = productService.create(member1, keyword2, "행복 도서", "임시 책내용","행복 도서에 대한 설명", 22000);
        Product product3 = productService.create(member1, keyword2, "슬픔 도서", "임시 책내용","슬픔 도서에 대한 설명", 1000);
        Product product4 = productService.create(member1, keyword2, "역경 도서", "임시 책내용","역경 도서에 대한 설명", 30000);

        CartItem cartItem1 = cartService.addItem(member3, product1);
        CartItem cartItem2 = cartService.addItem(member3, product2);
        CartItem cartItem3 = cartService.addItem(member4, product3);
        CartItem cartItem4 = cartService.addItem(member4, product4);

        memberService.addCash(member1, 10_000, "충전__무통장입금");
        memberService.addCash(member1, 20_000, "충전__무통장입금");
        memberService.addCash(member1, -5_000, "출금__일반");
        memberService.addCash(member1, 1_000_000, "충전__무통장입금");

        memberService.addCash(member2, 2_000_000, "충전__무통장입금");
    }
}