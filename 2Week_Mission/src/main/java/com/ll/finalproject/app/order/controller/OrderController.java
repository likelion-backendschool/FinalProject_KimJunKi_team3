package com.ll.finalproject.app.order.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ll.finalproject.app.base.rq.Rq;
import com.ll.finalproject.app.member.entity.Member;
import com.ll.finalproject.app.member.service.MemberService;
import com.ll.finalproject.app.order.entity.Order;
import com.ll.finalproject.app.order.exception.*;
import com.ll.finalproject.app.order.service.OrderService;
import com.ll.finalproject.util.Ut;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.*;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/order")
public class OrderController {

    private final OrderService orderService;
    private final Rq rq;
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper;
    private final MemberService memberService;

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public String showDetail(@PathVariable long id, Model model) {

        try {
            Order order = orderService.findForPrintById(id);
            Member actor = rq.getMember();

            long restCash = memberService.getRestCash(actor);
            orderService.actorCanSee(actor, order);
            model.addAttribute("order", order);
            model.addAttribute("actorRestCash", restCash);
        } catch (OrderNotFoundException e){
            return "";
        } catch (ActorCanNotSeeOrderException e) {
            return "";
        }

        return "order/detail";
    }

    private final String SECRET_KEY = "test_sk_BE92LAa5PVbDMx4BAzP87YmpXyJj";

    @RequestMapping("/{id}/success")
    public String confirmPayment(
            @PathVariable long id,
            @RequestParam String paymentKey,
            @RequestParam String orderId,
            @RequestParam Long amount,
            Model model
    ) throws Exception {

        Order order = orderService.findForPrintById(id);

        long orderIdInputed = Long.parseLong(orderId.split("__")[1]);

        if ( id != orderIdInputed ) {
            throw new OrderIdNotMatchedException();
        }
        HttpHeaders headers = new HttpHeaders();
        // headers.setBasicAuth(SECRET_KEY, ""); // spring framework 5.2 이상 버전에서 지원
        headers.set("Authorization", "Basic " + Base64.getEncoder().encodeToString((SECRET_KEY + ":").getBytes()));
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, String> payloadMap = new HashMap<>();
        payloadMap.put("orderId", orderId);
        payloadMap.put("amount", String.valueOf(amount));

        Member actor = rq.getMember();
        long restCash = memberService.getRestCash(actor);
        long payPriceRestCash = order.calculatePayPrice() - amount; // 예치금으로 결제할 금액

        if (payPriceRestCash > restCash) {
            throw new OrderNotEnoughRestCashException();
        }

        HttpEntity<String> request = new HttpEntity<>(objectMapper.writeValueAsString(payloadMap), headers);

        ResponseEntity<JsonNode> responseEntity = restTemplate.postForEntity(
                "https://api.tosspayments.com/v1/payments/" + paymentKey, request, JsonNode.class);

        if (responseEntity.getStatusCode() == HttpStatus.OK) {

            orderService.payByTossPayments(order, payPriceRestCash);

            JsonNode successNode = responseEntity.getBody();
            model.addAttribute("orderId", successNode.get("orderId").asText());
            String secret = successNode.get("secret").asText(); // 가상계좌의 경우 입금 callback 검증을 위해서 secret을 저장하기를 권장함
            return "order/success";
        } else {
            JsonNode failNode = responseEntity.getBody();
            model.addAttribute("message", failNode.get("message").asText());
            model.addAttribute("code", failNode.get("code").asText());
            return "order/fail";
        }
    }

    @RequestMapping("/{id}/fail")
    public String failPayment(@RequestParam String message, @RequestParam String code, Model model) {
        model.addAttribute("message", message);
        model.addAttribute("code", code);
        return "order/fail";
    }
    @PostMapping("/{id}/payByRestCashOnly")
    @PreAuthorize("isAuthenticated()")
    public String payByRestCashOnly(@PathVariable long id) {
        Order order = orderService.findForPrintById(id);

        Member actor = rq.getMember();

        if (orderService.actorCanPayment(actor, order) == false) {
            throw new ActorCanNotPayOrderException();
        }

        orderService.payByRestCashOnly(order);
        return "redirect:/order/%d?msg=%s".formatted(order.getId(), Ut.url.encode("예치금으로 결제했습니다."));
    }

    @PostMapping("/makeOrder")
    @PreAuthorize("isAuthenticated()")
    public String makeOrder() {
        Member member = rq.getMember();
        Order order = orderService.createFromCart(member);
        String redirect = "redirect:/order/%d".formatted(order.getId()) + "?msg=" + Ut.url.encode("%d번 주문이 생성되었습니다.".formatted(order.getId()));

        return redirect;
    }

    @PostMapping("/{id}/cancel")
    @PreAuthorize("isAuthenticated()")
    public String cancel(@PathVariable Long id) {
        Member actor = rq.getMember();

        orderService.cancelOrder(actor, id);

        return "redirect:/cart/items/?msg=%s".formatted(Ut.url.encode("주문이 취소되었습니다."));
    }

}
