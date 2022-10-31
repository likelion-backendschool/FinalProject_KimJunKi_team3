package com.ll.finalproject.app.service;

import com.ll.finalproject.app.member.entity.Member;
import com.ll.finalproject.app.member.repository.MemberRepository;
import com.ll.finalproject.app.post.keyword.entity.PostKeyword;
import com.ll.finalproject.app.post.keyword.service.PostKeywordService;
import com.ll.finalproject.app.product.entity.Product;
import com.ll.finalproject.app.product.service.ProductService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@ActiveProfiles({"test", "base"})
public class ProductServiceTests {

    @Autowired
    private ProductService productService;

    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private PostKeywordService postKeywordService;

    @Test
    @DisplayName("도서 등록")
    void t1() {
        Member author = memberRepository.findByUsername("user2").get();
        PostKeyword postKeyword1 = postKeywordService.findByContent("안녕").get();

        Product product3 = productService.create(author.getId(), "안녕 책 제목입니다", "이 책은 ...", 1999, postKeyword1.getId());

        assertThat(product3).isNotNull();
    }

    @Test
    @DisplayName("도서 수정")
    void t2() {
        Product product = productService.getProduct(1L);

        productService.modify(product.getId(), product.getAuthor().getId(), "상품명2 NEW", 70_000, "책 설명");

        assertThat(product.getSubject()).isEqualTo("상품명2 NEW");
        assertThat(product.getPrice()).isEqualTo(70_000);
    }

}