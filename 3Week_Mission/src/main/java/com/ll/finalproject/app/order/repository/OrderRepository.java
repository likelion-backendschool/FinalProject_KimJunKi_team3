package com.ll.finalproject.app.order.repository;

import com.ll.finalproject.app.order.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findAllByBuyerIdOrderByIdDesc(Long buyerId);
}