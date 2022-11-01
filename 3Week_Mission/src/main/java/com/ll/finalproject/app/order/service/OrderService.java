package com.ll.finalproject.app.order.service;

import com.ll.finalproject.app.base.dto.RsData;
import com.ll.finalproject.app.cart.entity.CartItem;
import com.ll.finalproject.app.cart.repository.CartItemRepository;
import com.ll.finalproject.app.cart.service.CartService;
import com.ll.finalproject.app.member.entity.Member;
import com.ll.finalproject.app.member.exception.MemberNotFoundException;
import com.ll.finalproject.app.member.repository.MemberRepository;
import com.ll.finalproject.app.member.service.MemberService;
import com.ll.finalproject.app.myBook.service.MyBookService;
import com.ll.finalproject.app.order.entity.Order;
import com.ll.finalproject.app.order.entity.OrderItem;
import com.ll.finalproject.app.order.exception.ActorCanNotSeeOrderException;
import com.ll.finalproject.app.order.exception.OrderNotFoundException;
import com.ll.finalproject.app.order.repository.OrderItemRepository;
import com.ll.finalproject.app.order.repository.OrderRepository;
import com.ll.finalproject.app.product.entity.Product;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {
    private final MemberService memberService;
    private final MemberRepository memberRepository;
    private final CartService cartService;
    private final CartItemRepository cartItemRepository;
    private final OrderRepository orderRepository;
    private final OrderItemService orderItemService;
    private final MyBookService myBookService;
    private final OrderItemRepository orderItemRepository;

    public Order createFromCart(String username) {
        // 입력된 회원의 장바구니 아이템들을 전부 가져온다.

        // 만약에 특정 장바구니의 상품옵션이 판매불능이면 삭제
        // 만약에 특정 장바구니의 상품옵션이 판매가능이면 주문품목으로 옮긴 후 삭제

        Member buyer = memberService.findByUsername(username);

        List<CartItem> cartItems = cartService.getCartItemsByBuyer(buyer.getId());

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

        payDone(order);
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
    public boolean actorCanSee(String username, Order order) {

        Member buyer = memberService.findByUsername(username);

        if (!buyer.getId().equals(order.getBuyer().getId())) {
            throw new ActorCanNotSeeOrderException("조회할 수 없는 주문입니다");
        }

        return true;
    }


    public void payByTossPayments(Order order, long useRestCash) {

        Member buyer = order.getBuyer();
        long restCash = buyer.getRestCash();
        int payPrice = order.calculatePayPrice();

        long pgPayPrice = payPrice - useRestCash;
        memberService.addCash(buyer, pgPayPrice, "주문__%d__충전__토스페이먼츠".formatted(order.getId()));
        memberService.addCash(buyer, pgPayPrice * -1, "주문__%d__사용__토스페이먼츠".formatted(order.getId()));

        if (useRestCash > 0) {
            if ( useRestCash > restCash ) {
                throw new RuntimeException("예치금이 부족합니다.");
            }

            memberService.addCash(buyer, useRestCash * -1, "주문__%d__사용__예치금".formatted(order.getId()));
        }

        payDone(order);
    }
    private void payDone(Order order) {
        order.setPaymentDone();
        myBookService.add(order);
        orderRepository.save(order);
    }
    public boolean actorCanPayment(String username, Order order) {
        return actorCanSee(username, order);
    }

    public void cancelOrder(String username, Long orderId) {

        // 주문 아이템을 다시 카트로 넘겨줌
        // 주문 아이템 삭제, 주문 삭제

        Member actor = memberService.findByUsername(username);

        Order order = getOrder(orderId);

        actorCanSee(actor.getUsername(), order);

        List<OrderItem> orderItems = orderItemService.getOrderItemsByOrder(order);

        for (OrderItem orderItem : orderItems) {
            Product product = orderItem.getProduct();

            cartItemRepository.save(new CartItem(product, actor));
            orderItemService.removeOrderItem(orderItem);
        }

        orderRepository.delete(order);
    }

    public RsData refund(Order order, Member actor) {
        RsData actorCanRefundRsData = actorCanRefund(actor.getId(), order);

        if (actorCanRefundRsData.isFail()) {
            return actorCanRefundRsData;
        }

        order.setCancelDone();

        int payPrice = order.getPayPrice();
        memberService.addCash(order.getBuyer(), payPrice, "주문__%d__환불__예치금".formatted(order.getId()));

        order.setRefundDone();
        orderRepository.save(order);

        myBookService.remove(order);

        return RsData.of("S-1", "환불되었습니다.");
    }

    public RsData refund(Long orderId, Long memberId) {
        Order order = getOrder(orderId);
        Member member = getMember(memberId);

        if (order == null) {
            return RsData.of("F-2", "결제 상품을 찾을 수 없습니다.");
        }
        return refund(order, member);
    }

    public RsData actorCanRefund(Long memberId, Order order) {

        if (order.isCanceled()) {
            return RsData.of("F-1", "이미 취소되었습니다.");
        }

        if ( order.isRefunded() ) {
            return RsData.of("F-4", "이미 환불되었습니다.");
        }

        if ( order.isPaid() == false ) {
            return RsData.of("F-5", "결제가 되어야 환불이 가능합니다.");
        }

        if (memberId.equals(order.getBuyer().getId()) == false) {
            return RsData.of("F-2", "권한이 없습니다.");
        }

        long between = ChronoUnit.MINUTES.between(order.getPayDate(), LocalDateTime.now());

        if (between > 10) {
            return RsData.of("F-3", "결제 된지 10분이 지났으므로, 환불 할 수 없습니다.");
        }

        return RsData.of("S-1", "환불할 수 있습니다.");
    }
    @Transactional(readOnly = true)
    public Order getOrder(Long orderId) {
        return orderRepository.findById(orderId).orElseThrow(
                () -> new OrderNotFoundException(""));
    }
    @Transactional(readOnly = true)
    public Member getMember(Long memberId) {
        return memberRepository.findById(memberId).orElseThrow(
                () -> new MemberNotFoundException(""));
    }

    public List<Order> findAllByBuyerId(Long buyerId) {
        return orderRepository.findAllByBuyerIdOrderByIdDesc(buyerId);
    }


    public List<OrderItem> findAllByPayDateBetweenOrderByIdAsc(LocalDateTime fromDate, LocalDateTime toDate) {
        return orderItemRepository.findAllByPayDateBetween(fromDate, toDate);
    }
}