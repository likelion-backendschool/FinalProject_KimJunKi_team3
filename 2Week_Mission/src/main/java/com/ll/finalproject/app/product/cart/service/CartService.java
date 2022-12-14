package com.ll.finalproject.app.product.cart.service;

import com.ll.finalproject.app.member.service.MemberService;
import com.ll.finalproject.app.product.cart.entity.CartItem;
import com.ll.finalproject.app.product.cart.exception.AlreadyExistsCartItemException;
import com.ll.finalproject.app.product.cart.repository.CartItemRepository;
import com.ll.finalproject.app.member.entity.Member;
import com.ll.finalproject.app.product.entity.Product;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class CartService {
    private final CartItemRepository cartItemRepository;
    private final MemberService memberService;

    public CartItem addItem(Long memberId, Product product) {

        Member buyer = memberService.findById(memberId);

        boolean hasCartItem = cartItemRepository.existsByBuyerIdAndProductId(buyer.getId(), product.getId());

        if (hasCartItem) {
            throw new AlreadyExistsCartItemException();
        }

        CartItem cartItem = CartItem.builder()
                .buyer(buyer)
                .product(product)
                .build();

        cartItemRepository.save(cartItem);

        return cartItem;
    }

    public boolean removeItem(Member buyer, Product product) {
        CartItem oldCartItem = cartItemRepository.findByBuyerIdAndProductId(buyer.getId(), product.getId()).orElse(null);

        if (oldCartItem != null) {
            cartItemRepository.delete(oldCartItem);
            return true;
        }

        return false;
    }
    public void removeItem(CartItem cartItem) {
        cartItemRepository.delete(cartItem);
    }

    public void removeItem(
            Member buyer,
            Long productId
    ) {
        Product product = new Product(productId);
        removeItem(buyer, product);
    }

    public boolean hasItem(Member buyer, Product product) {
        return cartItemRepository.existsByBuyerIdAndProductId(buyer.getId(), product.getId());
    }

    public List<CartItem> getCartItemsByBuyer(Long memberId) {
        Member buyer = memberService.findById(memberId);
        return cartItemRepository.findAllByBuyerId(buyer.getId());
    }

    public Optional<CartItem> findItemById(long id) {
        return cartItemRepository.findById(id);
    }

    public boolean actorCanDelete(Long memberId, CartItem cartItem) {
        Member buyer = memberService.findById(memberId);
        return buyer.getId().equals(cartItem.getBuyer().getId());
    }
}
