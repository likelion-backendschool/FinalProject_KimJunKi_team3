package com.ll.finalproject.app.order.service;

import com.ll.finalproject.app.cart.entity.CartItem;
import com.ll.finalproject.app.cart.repository.CartItemRepository;
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
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {
    private final MemberService memberService;
    private final CartService cartService;
    private final CartItemRepository cartItemRepository;
    private final OrderRepository orderRepository;
    private final OrderItemService orderItemService;

    public Order createFromCart(Member buyer) {
        // 입력된 회원의 장바구니 아이템들을 전부 가져온다.

        // 만약에 특정 장바구니의 상품옵션이 판매불능이면 삭제
        // 만약에 특정 장바구니의 상품옵션이 판매가능이면 주문품목으로 옮긴 후 삭제

        List<CartItem> cartItems = cartService.getCartItemsByBuyer(buyer);

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


    public void payByRestCashOnly(Order order) {
        Member buyer = order.getBuyer();

        long restCash = buyer.getRestCash();

        int payPrice = order.calculatePayPrice();

        if (payPrice > restCash) {
            throw new RuntimeException("예치금이 부족합니다.");
        }

        memberService.addCash(buyer, payPrice * -1, "주문__%d__사용__예치금".formatted(order.getId()));

        order.setPaymentDone();
        orderRepository.save(order);
    }


    public void refund(Order order) {
        int payPrice = order.getPayPrice();
        memberService.addCash(order.getBuyer(), payPrice, "주문__%d__환불__예치금".formatted(order.getId()));

        order.setRefundDone();
        orderRepository.save(order);
    }

    @Transactional(readOnly = true)
    public Order findForPrintById(long id) {
        return orderRepository.findById(id).orElseThrow(
                () -> new OrderNotFoundException("해당 주문은 존재하지 않습니다")
        );
    }

    @Transactional(readOnly = true)
    public boolean actorCanSee(Member actor, Order order) {
        if (!actor.getId().equals(order.getBuyer().getId())) {
            throw new ActorCanNotSeeOrderException("조회할 수 없는 주문입니다");
        }
        return true;
    }


    public void payByTossPayments(Order order, long useRestCash) {
        Member buyer = order.getBuyer();
        int payPrice = order.calculatePayPrice();

        long pgPayPrice = payPrice - useRestCash;
        memberService.addCash(buyer, pgPayPrice, "주문__%d__충전__토스페이먼츠".formatted(order.getId()));
        memberService.addCash(buyer, pgPayPrice * -1, "주문__%d__사용__토스페이먼츠".formatted(order.getId()));

        if ( useRestCash > 0 ) {
            memberService.addCash(buyer, useRestCash * -1, "주문__%d__사용__예치금".formatted(order.getId()));
        }

        order.setPaymentDone();
        orderRepository.save(order);
    }

    public boolean actorCanPayment(Member actor, Order order) {
        return actorCanSee(actor, order);
    }

    public void cancelOrder(Member actor, Long id) {

        // 주문 아이템을 다시 카트로 넘겨줌
        // 주문 아이템 삭제, 주문 삭제

        Order order = findOrderById(id).orElseThrow(
                () -> new OrderNotFoundException("존재하지 않습니다.")
        );

        actorCanSee(actor, order);

        List<OrderItem> orderItems = orderItemService.getOrderItemsByOrder(order);

        for (OrderItem orderItem : orderItems) {
            Product product = orderItem.getProduct();

            cartItemRepository.save(new CartItem(product, actor));
            orderItemService.removeOrderItem(orderItem);
        }

        orderRepository.delete(order);
    }

    @Transactional(readOnly = true)
    public Optional<Order> findOrderById(long id) {
        return orderRepository.findById(id);
    }
}