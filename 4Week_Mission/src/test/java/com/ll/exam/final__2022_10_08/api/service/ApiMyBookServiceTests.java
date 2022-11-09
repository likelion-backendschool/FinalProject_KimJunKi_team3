package com.ll.exam.final__2022_10_08.api.service;

import com.ll.exam.final__2022_10_08.api.common.exception.ExceptionType;
import com.ll.exam.final__2022_10_08.api.common.exception.MyBookInvalidException;
import com.ll.exam.final__2022_10_08.api.myBook.dto.MyBookResponse;
import com.ll.exam.final__2022_10_08.api.myBook.dto.MyBooksResponse;
import com.ll.exam.final__2022_10_08.api.post.dto.PostResponse;
import com.ll.exam.final__2022_10_08.api.product.dto.ProductResponse;
import com.ll.exam.final__2022_10_08.app.member.entity.Member;
import com.ll.exam.final__2022_10_08.app.member.service.MemberService;
import com.ll.exam.final__2022_10_08.app.myBook.entity.MyBook;
import com.ll.exam.final__2022_10_08.app.myBook.repository.MyBookRepository;
import com.ll.exam.final__2022_10_08.app.myBook.service.MyBookService;

import com.ll.exam.final__2022_10_08.app.post.entity.Post;
import com.ll.exam.final__2022_10_08.app.postTag.entity.PostTag;
import com.ll.exam.final__2022_10_08.app.postTag.repository.PostTagRepository;
import com.ll.exam.final__2022_10_08.app.product.entity.Product;
import com.ll.exam.final__2022_10_08.app.product.repository.ProductRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.test.context.ActiveProfiles;

import org.springframework.transaction.annotation.Transactional;


import java.util.List;

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
public class ApiMyBookServiceTests {

    @Autowired
    private MyBookService myBookService;
    @Autowired
    private MyBookRepository myBookRepository;
    @Autowired
    private MemberService memberService;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private PostTagRepository postTagRepository;

    @Test
    @DisplayName("getMyBooks")
    void t1() {
        // When
        Member member = memberService.findByUsername("user1").get();
        MyBooksResponse myBooks = myBookService.getMyBooksResponse(member.getId());

        // Then
        System.out.println("myBooks = " + myBooks);
    }

    @Test
    @DisplayName("getMyBook 존재하지 않은 id일 경우 MyBookInvalidException 예외 ")
    void t2() {
        // When
        Member member = memberService.findByUsername("user1").get();

        // Then
        assertThatThrownBy(
                () -> myBookService.getMyBookResponse(1000L, member.getId())
        ).isInstanceOf(MyBookInvalidException.class)
                .hasMessageContaining(ExceptionType.MYBOOK_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("getMyBook 유저가 도서에 대한 권한이 없는 경우 ")
    void t3() {
        // Then
        assertThatThrownBy(
                () -> myBookService.getMyBookResponse(1L, 1000L)
        ).isInstanceOf(MyBookInvalidException.class)
                .hasMessageContaining(ExceptionType.INVALID_MYBOOK_OWNER.getMessage());
    }

    @Test
    @DisplayName("getMyBookResponse 데이터 잘 가져오는지 테스트")
    void t4() {
        //given
        MyBook myBook = myBookRepository.findById(1L).get();

        Product product = productRepository.findById(myBook.getProduct().getId()).get();
        ProductResponse productResponse = ProductResponse.of(product);

        //when
        MyBookResponse myBookResponse = myBookService.getMyBookResponse(myBook.getId(), myBook.getOwner().getId());

        //then
        assertThat(myBookResponse.getId()).isEqualTo(myBook.getId());
        assertThat(myBookResponse.getProduct().getId()).isEqualTo(myBook.getProduct().getId());
        assertThat(myBookResponse.getOwnerId()).isEqualTo(myBook.getOwner().getId());

        assertThat(myBookResponse.getProduct().getSubject()).isEqualTo(productResponse.getSubject());
    }

    @Test
    @DisplayName("getBookChapter 데이터 잘 가져오는지 테스트")
    void t5() {
        //given
        MyBook myBook = myBookRepository.findById(1L).get();

        List<PostTag> postTags = postTagRepository.findAllByPostKeyword(myBook.getProduct().getPostKeyword());

        List<Post> posts = postTags.stream()
                .map(PostTag::getPost)
                .collect(toList());

        //when
        List<PostResponse> postResponseList = myBookService.getBookChapter(myBook);

        //then
        assertThat(postResponseList.size()).isEqualTo(posts.size());

        for (int i = 0; i < postResponseList.size(); i++) {
            assertThat(postResponseList.get(i).getId()).isEqualTo(posts.get(i).getId());
            assertThat(postResponseList.get(i).getSubject()).isEqualTo(posts.get(i).getSubject());
            assertThat(postResponseList.get(i).getContent()).isEqualTo(posts.get(i).getContent());
        }

    }
}
