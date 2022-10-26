package com.ll.finalproject.app.cart.controller;

import com.ll.finalproject.app.base.rq.Rq;
import com.ll.finalproject.app.cart.entity.CartItem;
import com.ll.finalproject.app.cart.exception.AlreadyExistsCartItemException;
import com.ll.finalproject.app.cart.service.CartService;
import com.ll.finalproject.app.member.entity.Member;
import com.ll.finalproject.app.member.exception.*;
import com.ll.finalproject.app.member.form.*;
import com.ll.finalproject.app.member.service.MemberService;
import com.ll.finalproject.app.product.entity.Product;
import com.ll.finalproject.app.product.service.ProductService;
import com.ll.finalproject.util.Ut;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.security.Principal;
import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping("/cart")
@RequiredArgsConstructor
@Slf4j
public class CartController {

    private final CartService cartService;
    private final ProductService productService;
    private final Rq rq;

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/items")
    public String showItems(Model model) {
        Member buyer = rq.getMember();
        List<CartItem> items = cartService.getItemsByBuyer(buyer);

        model.addAttribute("items", items);
        return "cart/items";
    }

    @PostMapping("/removeItems")
    @PreAuthorize("isAuthenticated()")
    public String removeItems(String ids) {
        Member buyer = rq.getMember();

        String[] idsArr = ids.split(",");

        Arrays.stream(idsArr)
                .mapToLong(Long::parseLong)
                .forEach(id -> {
                    CartItem cartItem = cartService.findItemById(id).orElse(null);

                    if (cartService.actorCanDelete(buyer, cartItem)) {
                        cartService.removeItem(cartItem);
                    }
                });

        return "redirect:/cart/items?msg=" + Ut.url.encode("%d건의 품목을 삭제하였습니다.".formatted(idsArr.length));
    }

    @PostMapping("/add/{productId}")
    @PreAuthorize("isAuthenticated()")
    public String addItem(@PathVariable Long productId) {
        Member buyer = rq.getMember();

        Product product = productService.getProductById(productId);

        try {
            cartService.addItem(buyer, product);
        } catch (AlreadyExistsCartItemException e) {
            return "redirect:/product/%d/?msg=%s".formatted(productId, Ut.url.encode("이미 추가됐습니다."));
        }

        return "redirect:/product/%d/?msg=%s".formatted(productId, Ut.url.encode("장바구니에 추가되었습니다."));
    }

}
