package com.ll.finalproject.app.base.initData;

import com.ll.finalproject.app.cart.entity.CartItem;
import com.ll.finalproject.app.cart.service.CartService;
import com.ll.finalproject.app.member.entity.Member;
import com.ll.finalproject.app.member.service.MemberService;
import com.ll.finalproject.app.order.entity.Order;
import com.ll.finalproject.app.order.service.OrderService;
import com.ll.finalproject.app.post.keyword.entity.PostKeyword;
import com.ll.finalproject.app.post.keyword.service.PostKeywordService;
import com.ll.finalproject.app.post.service.PostService;
import com.ll.finalproject.app.product.entity.Product;
import com.ll.finalproject.app.product.service.ProductService;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public interface InitDataBefore {
    default void before(MemberService memberService,
                        PostService postService,
                        ProductService productService,
                        PostKeywordService postKeywordService,
                        CartService cartService,
                        OrderService orderService
    ) {

        /**
         * user1은 #그리움 #행복 #슬픔 #역경 태그가 달린 글 작성
         * user1은 #그리움 #행복 #슬픔 #역경 각각 태그에 대한 도서 등록
         * user3은 user1의 도서1, 도서2를 장바구니에 추가
         * user4은 user1의 도서3, 도서4를 장바구니에 추가
         *
         * user1은 각 키워드에 대한 4권의 도서 등록
         *
         * user3과 user4에 예치금 추가
         */

        class Helper {
            public Order order(Member member, List<Product> products) {
                for (int i = 0; i < products.size(); i++) {
                    Product product = products.get(i);

                    cartService.addItem(member, product);
                }

                return orderService.createFromCart(member);
            }
        }

        Helper helper = new Helper();

        Member member1 = memberService.join("user1", "1234!","user1@test.com");
        Member member2 = memberService.join("user2", "1234!","user2@test.com");

        Member member3 = memberService.join("user3", "1234!","user3@test.com");
        Member member4 = memberService.join("user4", "1234!","user4@test.com");

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
        Product product3 = productService.create(member1, keyword3, "슬픔 도서", "임시 책내용","슬픔 도서에 대한 설명", 1000);
        Product product4 = productService.create(member1, keyword4, "역경 도서", "임시 책내용","역경 도서에 대한 설명", 30000);

        memberService.addCash(member3, 10_000, "충전__무통장입금");
        memberService.addCash(member3, 20_000, "충전__무통장입금");
        memberService.addCash(member3, -5_000, "출금__일반");
        memberService.addCash(member3, 1_000_000, "충전__무통장입금");
        memberService.addCash(member4, 2_000_000, "충전__무통장입금");

        // 1번 주문 : 결제완료
        Order order1 = helper.order(member3, Arrays.asList(
                        product1,
                        product2
                )
        );

        int order1PayPrice = order1.calculatePayPrice();
        orderService.payByRestCashOnly(order1);

        // 2번 주문 : 결제 후 환불
        Order order2 = helper.order(member4, Arrays.asList(
                        product3,
                        product4
                )
        );

        orderService.payByRestCashOnly(order2);

        orderService.refund(order2);

        // 3번 주문 : 결제 전
        Order order3 = helper.order(member4, Arrays.asList(
                        product1,
                        product2
                )
        );
    }
}