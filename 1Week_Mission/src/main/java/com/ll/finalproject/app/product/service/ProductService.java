package com.ll.finalproject.app.product.service;

import com.ll.finalproject.app.member.entity.Member;
import com.ll.finalproject.app.post.entity.Post;
import com.ll.finalproject.app.post.keyword.entity.PostKeyword;
import com.ll.finalproject.app.product.entity.Product;
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

    public Product create(Member member, PostKeyword postKeyword, String subject, String content, String description, int price) {

        Product product = Product
                .builder()
                .author(member)
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

    public Product getProductById(Long id) {
        return findById(id).orElse(null);
    }

    @Transactional(readOnly = true)
    public Optional<Product> findById(Long id) {
        return productRepository.findById(id);
    }
}
