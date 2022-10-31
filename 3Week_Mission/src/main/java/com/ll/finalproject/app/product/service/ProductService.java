package com.ll.finalproject.app.product.service;

import com.ll.finalproject.app.member.entity.Member;
import com.ll.finalproject.app.member.exception.MemberNotFoundException;
import com.ll.finalproject.app.member.repository.MemberRepository;
import com.ll.finalproject.app.post.entity.Post;
import com.ll.finalproject.app.post.exception.NoAuthorizationException;
import com.ll.finalproject.app.post.keyword.entity.PostKeyword;
import com.ll.finalproject.app.post.keyword.repository.PostKeywordRepository;
import com.ll.finalproject.app.post.repository.PostRepository;
import com.ll.finalproject.app.product.dto.ProductDto;
import com.ll.finalproject.app.product.entity.Product;
import com.ll.finalproject.app.product.exception.ProductNotFoundException;
import com.ll.finalproject.app.product.mapper.ProductMapper;
import com.ll.finalproject.app.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ProductService {

    final private ProductRepository productRepository;
    final private PostKeywordRepository postKeywordRepository;
    final private MemberRepository memberRepository;
    final private PostRepository postRepository;

    @Transactional(readOnly = true)
    public Product getProduct(Long productId) {
        return productRepository.findById(productId).orElseThrow(
                () -> new ProductNotFoundException());
    }

    @Transactional(readOnly = true)
    public Member getAuthor(Long authorId) {
        return memberRepository.findById(authorId).orElseThrow(
                () -> new MemberNotFoundException("존재하지 않는 회원입니다."));
    }

    @Transactional(readOnly = true)
    public PostKeyword getPostKeyword(Long postKeywordId) {
        return postKeywordRepository.findById(postKeywordId).orElseThrow(
                () -> new RuntimeException()
        );
    }

    public Product create(Long authorId, String subject, String description, int price, Long postKeywordId) {

        Member author  = getAuthor(authorId);
        PostKeyword postKeyword = getPostKeyword(postKeywordId);

        // 선택한 태그에 해당하는 글들의 내용 합치기
        StringBuffer contentSb = new StringBuffer();
        for (Post post : postRepository.findAllByMemberIdAndPostKeyWordId(authorId, postKeywordId)) {
            contentSb.append(post.getContent() + "\n");
        }

        // 유저가 쓴 post 중 키워드가 id인 것들
        Product product = Product
                .builder()
                .author(author)
                .postKeyword(postKeyword)
                .subject(subject)
                .content(contentSb.toString())
                .description(description)
                .price(price)
                .build();

        productRepository.save(product);
        return product;
    }

    public void delete(Long productId, Long authorId) {

        Product product = getProduct(productId);
        Member author = getAuthor(authorId);

        validateAuthor(product, author);

        productRepository.delete(product);
    }

    public void modify(Long productId, Long authorId, String subject, Integer price, String description) {

        Product product = getProduct(productId);
        Member author = getAuthor(authorId);

        validateAuthor(product, author);

        product.changeModifyForm(subject, price, description);
    }

    public List<ProductDto> getProductList() {
        List<Product> products = productRepository.findAll();
        return ProductMapper.INSTANCE.entitiesToProductDtos(products);
    }

    public ProductDto getProductById(Long productId) {
        Product product = getProduct(productId);
        return ProductMapper.INSTANCE.entityToProductDto(product);
    }

    public ProductDto getProductById(Long productId, long authorId) {
        Product product = getProduct(productId);
        Member author = getAuthor(authorId);

        validateAuthor(product, author);

        return ProductMapper.INSTANCE.entityToProductDto(product);
    }

    private void validateAuthor(Product product, Member author) {
        if (!product.isAuthor(author)) {
            throw new NoAuthorizationException("해당 글의 수정 권한이 없습니다.");
        }
    }

}
