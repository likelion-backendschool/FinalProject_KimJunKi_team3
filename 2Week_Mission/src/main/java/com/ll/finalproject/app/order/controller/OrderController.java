package com.ll.finalproject.app.order.controller;

import com.ll.finalproject.app.base.rq.Rq;
import com.ll.finalproject.app.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/order")
public class OrderController {

    private final OrderService orderService;
    private final Rq rq;


//    @GetMapping("/{id}")
//    public String showDetail(@PathVariable long id) {
//
//    }

}
