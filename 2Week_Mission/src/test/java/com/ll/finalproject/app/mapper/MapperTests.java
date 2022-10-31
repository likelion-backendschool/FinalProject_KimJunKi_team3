package com.ll.finalproject.app.mapper;

import com.ll.finalproject.app.member.entity.Member;
import com.ll.finalproject.app.member.dto.MemberJoinForm;
import com.ll.finalproject.app.member.mapper.MemberMapper;
import com.ll.finalproject.app.post.entity.Post;
import com.ll.finalproject.app.post.dto.PostDto;
import com.ll.finalproject.app.post.keyword.entity.PostKeyword;
import com.ll.finalproject.app.post.mapper.PostMapper;
import com.ll.finalproject.app.post.repository.PostRepository;
import com.ll.finalproject.app.product.dto.ProductDto;
import com.ll.finalproject.app.product.entity.Product;
import com.ll.finalproject.app.product.mapper.ProductMapper;
import com.ll.finalproject.app.product.repository.ProductRepository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@ActiveProfiles({"test", "base"})
public class MapperTests {
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private ProductRepository productRepository;

    @Test
    public void memberToMemberJoinForm() {
        //given
        Member member = Member.builder()
                .username("hello123")
                .email("hello123@test.com")
                .password("123")
                .build();

        //when
        MemberJoinForm memberJoinForm = MemberMapper.INSTANCE.entityToMemberJoinForm(member);

        System.out.println("member = " + member);
        System.out.println("memberJoinForm = " + memberJoinForm);

        //then
        assertThat( memberJoinForm ).isNotNull();
        assertThat( memberJoinForm.getUsername() ).isEqualTo( "hello123" );
        assertThat( memberJoinForm.getEmail() ).isEqualTo( "hello123@test.com" );
        assertThat( memberJoinForm.getPassword() ).isEqualTo( "123" );
        assertThat( memberJoinForm.getPasswordConfirm() ).isNull();

    }
    @Test
    public void entityToPostDto() {
        //given
        Post post = postRepository.findById(2L).get();

        //when
        PostDto postDto = PostMapper.INSTANCE.entityToPostDto(post);

        for (PostKeyword postKeyword : postDto.getPostHashTagList()) {
            System.out.println("postKeyword = " + postKeyword);
        }
        //then
        assertThat( postDto ).isNotNull();
        assertThat( postDto.getAuthor() ).isEqualTo(post.getAuthor().getUsername());
        assertThat( postDto.getContent() ).isEqualTo(post.getContent());
        assertThat( postDto.getContentHtml()).isEqualTo(post.getContentHtml());
    }

    @Test
    public void entityToProductDto() {
        //given
        Product product = productRepository.findById(2L).get();

        //when
        ProductDto productDto = ProductMapper.INSTANCE.entityToProductDto(product);

        //then
        assertThat( productDto ).isNotNull();
        assertThat( productDto.getAuthor().getUsername() ).isEqualTo(product.getAuthor().getUsername());
        assertThat( productDto.getSubject() ).isEqualTo(product.getSubject());
        assertThat( productDto.getContent()).isEqualTo(product.getContent());
        assertThat( productDto.getDescription()).isEqualTo(product.getDescription());
        assertThat( productDto.getPrice()).isEqualTo(product.getPrice());
        assertThat( productDto.getPostKeyword()).isEqualTo(product.getPostKeyword());
    }
}
