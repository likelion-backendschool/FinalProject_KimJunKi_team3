package com.ll.finalproject.app.service;

import com.ll.finalproject.app.cart.entity.CartItem;
import com.ll.finalproject.app.cart.service.CartService;
import com.ll.finalproject.app.member.entity.Member;
import com.ll.finalproject.app.member.repository.MemberRepository;
import com.ll.finalproject.app.order.entity.Order;
import com.ll.finalproject.app.order.entity.OrderItem;
import com.ll.finalproject.app.order.repository.OrderRepository;
import com.ll.finalproject.app.order.service.OrderItemService;
import com.ll.finalproject.app.order.service.OrderService;
import com.ll.finalproject.app.product.entity.Product;
import com.ll.finalproject.app.product.service.ProductService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@ActiveProfiles({"test", "base"})
public class OrderServiceTests {

    @Autowired
    private ProductService productService;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private OrderService orderService;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private CartService cartService;
    @Autowired
    private OrderItemService orderItemService;

    @Test
    @DisplayName("주문 취소")
    void t1() {

        Member buyer = memberRepository.findByUsername("user4").get();

        Order order1 = orderService.findOrderById(3).get();
        List<CartItem> cartItemsByBuyer1 = cartService.getCartItemsByBuyer(buyer);
        List<OrderItem> orderItemsByOrder1 = orderItemService.getOrderItemsByOrder(order1);

        assertThat(order1).isNotNull();
        assertThat(cartItemsByBuyer1.size()).isEqualTo(0);
        assertThat(orderItemsByOrder1.size()).isEqualTo(2);

        orderService.cancelOrder(buyer, order1.getId());

        Order order2 = orderService.findOrderById(3).orElse(null);
        List<CartItem> cartItemsByBuyer2 = cartService.getCartItemsByBuyer(buyer);
        List<OrderItem> orderItemsByOrder2 = orderItemService.getOrderItemsByOrder(order1);

        assertThat(order2).isNull();
        assertThat(cartItemsByBuyer2.size()).isEqualTo(2);
        assertThat(orderItemsByOrder2.size()).isEqualTo(0);
    }


}