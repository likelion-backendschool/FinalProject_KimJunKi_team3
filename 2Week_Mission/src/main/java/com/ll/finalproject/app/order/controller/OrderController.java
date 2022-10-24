package com.ll.finalproject.app.order.controller;

import com.ll.finalproject.app.base.rq.Rq;
import com.ll.finalproject.app.member.entity.Member;
import com.ll.finalproject.app.order.entity.Order;
import com.ll.finalproject.app.order.exception.ActorCanNotSeeOrderException;
import com.ll.finalproject.app.order.exception.OrderNotFoundException;
import com.ll.finalproject.app.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public String showDetail(@PathVariable long id, Model model) {

        try {
            Order order = orderService.findForPrintById(id);
            Member actor = rq.getMember();

            orderService.actorCanSee(actor, order);
            model.addAttribute("order", order);
        } catch (OrderNotFoundException e){
            return "";
        } catch (ActorCanNotSeeOrderException e) {
            return "";
        }

        return "order/detail";
    }

}
