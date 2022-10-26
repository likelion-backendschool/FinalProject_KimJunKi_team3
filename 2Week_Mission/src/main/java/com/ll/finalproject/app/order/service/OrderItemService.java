package com.ll.finalproject.app.order.service;

import com.ll.finalproject.app.order.entity.Order;
import com.ll.finalproject.app.order.entity.OrderItem;
import com.ll.finalproject.app.order.repository.OrderItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderItemService {

    private final OrderItemRepository orderItemRepository;

    public List<OrderItem> getOrderItemsByOrder(Order order) {
        return orderItemRepository.findAllByOrderId(order.getId());
    }

    public void removeOrderItem(OrderItem orderItem) {
        orderItemRepository.delete(orderItem);
    }
}
