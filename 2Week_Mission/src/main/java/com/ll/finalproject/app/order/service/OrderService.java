package com.ll.finalproject.app.order.service;

import com.ll.finalproject.app.cart.entity.CartItem;
import com.ll.finalproject.app.cart.service.CartService;
import com.ll.finalproject.app.member.entity.Member;
import com.ll.finalproject.app.member.service.MemberService;
import com.ll.finalproject.app.order.entity.Order;
import com.ll.finalproject.app.order.entity.OrderItem;
import com.ll.finalproject.app.order.exception.ActorCanNotSeeOrderException;
import com.ll.finalproject.app.order.exception.OrderNotFoundException;
import com.ll.finalproject.app.order.repository.OrderRepository;
import com.ll.finalproject.app.product.entity.Product;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderService {
    private final MemberService memberService;
    private final CartService cartService;
    private final OrderRepository orderRepository;

    @Transactional
    public Order createFromCart(Member buyer) {
        // 입력된 회원의 장바구니 아이템들을 전부 가져온다.

        // 만약에 특정 장바구니의 상품옵션이 판매불능이면 삭제
        // 만약에 특정 장바구니의 상품옵션이 판매가능이면 주문품목으로 옮긴 후 삭제

        List<CartItem> cartItems = cartService.getItemsByBuyer(buyer);

        List<OrderItem> orderItems = new ArrayList<>();

        for (CartItem cartItem : cartItems) {
            Product product = cartItem.getProduct();

            if (product.isOrderable()) {
                orderItems.add(new OrderItem(product));
            }

            cartService.removeItem(cartItem);
        }

        return create(buyer, orderItems);
    }

    @Transactional
    public Order create(Member buyer, List<OrderItem> orderItems) {
        Order order = Order
                .builder()
                .buyer(buyer)
                .build();

        for (OrderItem orderItem : orderItems) {
            order.addOrderItem(orderItem);
        }
        // 주문 품목으로 부터 이름을 만든다.
        order.makeName();
        orderRepository.save(order);

        return order;
    }

    @Transactional
    public void payByRestCashOnly(Order order) {
        Member buyer = order.getBuyer();

        long restCash = buyer.getRestCash();

        int payPrice = order.calculatePayPrice();

        if (payPrice > restCash) {
            throw new RuntimeException("예치금이 부족합니다.");
        }

        memberService.addCash(buyer, payPrice * -1, "주문결제__예치금결제");

        order.setPaymentDone();
        orderRepository.save(order);
    }

    @Transactional
    public void refund(Order order) {
        int payPrice = order.getPayPrice();
        memberService.addCash(order.getBuyer(), payPrice, "주문환불__예치금환불");

        order.setRefundDone();
        orderRepository.save(order);
    }

    public Order findForPrintById(long id) {
        return orderRepository.findById(id).orElseThrow(
                () -> new OrderNotFoundException("해당 주문은 존재하지 않습니다")
        );
    }

    public boolean actorCanSee(Member actor, Order order) {
        if (!actor.getId().equals(order.getBuyer().getId())) {
            throw new ActorCanNotSeeOrderException("조회할 수 없는 주문입니다");
        }
        return true;
    }

    @Transactional
    public void payByTossPayments(Order order) {
        Member buyer = order.getBuyer();
        int payPrice = order.calculatePayPrice();

        memberService.addCash(buyer, payPrice, "주문결제충전__토스페이먼츠");
        memberService.addCash(buyer, payPrice * -1, "주문결제__토스페이먼츠");

        order.setPaymentDone();
        orderRepository.save(order);
    }
}