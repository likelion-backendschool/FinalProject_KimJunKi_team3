package com.ll.finalproject.app.product.service;

import com.ll.finalproject.app.member.entity.Member;
import com.ll.finalproject.app.member.service.MemberService;
import com.ll.finalproject.app.post.keyword.entity.PostKeyword;
import com.ll.finalproject.app.product.entity.Product;
import com.ll.finalproject.app.product.exception.ProductNotFoundException;
import com.ll.finalproject.app.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {

    final private ProductRepository productRepository;
    final private MemberService memberService;

    public Product create(Long authorId, PostKeyword postKeyword, String subject, String content, String description, int price) {

        Member author  = memberService.findById(authorId);

        Product product = Product
                .builder()
                .author(author)
                .postKeyword(postKeyword)
                .subject(subject)
                .content(content)
                .description(description)
                .price(price)
                .build();

        productRepository.save(product);
        return product;
    }

    public List<Product> getProductList() {
        return productRepository.findAll();
    }

    public Product getProductById(Long productId) {
        return findById(productId).orElseThrow(
                () -> new ProductNotFoundException()
        );
    }

    @Transactional(readOnly = true)
    public Optional<Product> findById(long productId) {
        return productRepository.findById(productId);
    }

    public void delete(Product product) {
        productRepository.delete(product);
    }

    public void modify(Product product, String subject, Integer price, String description) {
        product.changeModifyForm(subject, price, description);
        productRepository.save(product);
    }
}
