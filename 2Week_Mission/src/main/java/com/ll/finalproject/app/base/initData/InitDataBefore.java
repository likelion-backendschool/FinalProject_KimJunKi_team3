package com.ll.finalproject.app.base.initData;

import com.ll.finalproject.app.product.cart.service.CartService;
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
         * user4는 user1의 도서3, 도서4를 장바구니에 추가
         *
         * user1은 각 키워드에 대한 4권의 도서 등록
         *
         * user3과 user4에 예치금 추가
         * user2는 도서1,2,3,4를 장바구니에 추가
         */

        class Helper {
            public Order order(Member member, List<Product> products) {
                for (int i = 0; i < products.size(); i++) {
                    Product product = products.get(i);

                    cartService.addItem(member.getId(), product);
                }

                return orderService.createFromCart(member.getUsername());
            }
        }

        Helper helper = new Helper();

        Member member1 = memberService.join("user1", "1234!","user1@test.com");
        Member member2 = memberService.join("user2", "1234!","user2@test.com");

        Member member3 = memberService.join("user3", "1234!","user3@test.com");
        Member member4 = memberService.join("user4", "1234!","user4@test.com");

        // 작가용
        Member member5 = memberService.join("user5", "1234!","user5@test.com");
        memberService.beAuthor(member5.getId(), "유저5작가");
        postService.write(member5.getId(), "제목 ", "내용 ","#작가테스트");

        for (int i = 0; i < 10; i++) {
            postService.write(member1.getId(), "제목 " + i, "내용 " + i,"#그리움 #행복 #슬픔 #역경");

        }
        for (int i = 0; i < 10; i++) {
            postService.write(member1.getId(), "제목 " + i, "내용 " + i,null);
        }
        postService.write(member2.getId(), "제목 ", "내용 ", "#안녕");

        PostKeyword keyword1 = postKeywordService.findByContent("그리움").get();
        PostKeyword keyword2 = postKeywordService.findByContent("행복").get();
        PostKeyword keyword3 = postKeywordService.findByContent("슬픔").get();
        PostKeyword keyword4 = postKeywordService.findByContent("역경").get();

        Product product1 = productService.create(member1.getId(),"그리움 도서", "그리움 도서에 대한 설명",12000, keyword1.getId() );
        Product product2 = productService.create(member1.getId(), "행복 도서","행복 도서에 대한 설명", 22000, keyword2.getId());
        Product product3 = productService.create(member1.getId(),"슬픔 도서","슬픔 도서에 대한 설명", 1000, keyword3.getId());
        Product product4 = productService.create(member1.getId(), "역경 도서","역경 도서에 대한 설명", 30000, keyword4.getId());

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

        cartService.addItem(member2.getId(), product1);
        cartService.addItem(member2.getId(), product2);
        cartService.addItem(member2.getId(), product3);
        cartService.addItem(member2.getId(), product4);
    }
}