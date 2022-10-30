package com.ll.finalproject.app.mapper;

import com.ll.finalproject.app.member.entity.Member;
import com.ll.finalproject.app.member.dto.MemberJoinForm;
import com.ll.finalproject.app.member.mapper.MemberMapper;
import com.ll.finalproject.app.member.repository.MemberRepository;
import com.ll.finalproject.app.post.entity.Post;
import com.ll.finalproject.app.post.entity.PostDto;
import com.ll.finalproject.app.post.keyword.entity.PostKeyword;
import com.ll.finalproject.app.post.mapper.PostMapper;
import com.ll.finalproject.app.post.repository.PostRepository;
import com.ll.finalproject.app.post.service.PostService;
import groovy.util.logging.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@ActiveProfiles({"test", "base"})
public class MapperTests {
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private PostService postService;
    @Autowired
    private PostRepository postRepository;

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

//        assertThat( postDto).isNull();

    }
}
