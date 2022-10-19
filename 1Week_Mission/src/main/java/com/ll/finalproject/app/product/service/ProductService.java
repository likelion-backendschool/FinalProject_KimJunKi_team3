package com.ll.finalproject.app.product.service;

import com.ll.finalproject.app.member.entity.Member;
import com.ll.finalproject.app.post.keyword.entity.PostKeyword;
import com.ll.finalproject.app.product.entity.Product;
import com.ll.finalproject.app.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {

    final private ProductRepository productRepository;

    public Product create(Member member, PostKeyword postKeyword, String subject, int price) {

        Product product = Product
                .builder()
                .author(member)
                .postKeyword(postKeyword)
                .subject(subject)
                .price(price)
                .build();

        productRepository.save(product);
        return product;
    }
}
