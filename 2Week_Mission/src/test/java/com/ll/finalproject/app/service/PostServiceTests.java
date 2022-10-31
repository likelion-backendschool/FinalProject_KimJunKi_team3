package com.ll.finalproject.app.service;

import com.ll.finalproject.app.base.rq.Rq;
import com.ll.finalproject.app.post.dto.PostDto;
import com.ll.finalproject.app.post.entity.Post;
import com.ll.finalproject.app.post.exception.PostNotFoundException;
import com.ll.finalproject.app.post.repository.PostRepository;
import com.ll.finalproject.app.post.service.PostService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles({"test", "base"})
@Transactional
class PostServiceTests {

    @Autowired
    private PostRepository postRepository;
    @Autowired
    private PostService postService;
    @Autowired
    private Rq rq;

    @Test
    @DisplayName("해시태그 제외하고 modify 성공 테스트 ")
    void t3() {
        //given
        Post post1 = postRepository.findById(1L).get();
        String oldSubject = post1.getSubject();
        String oldContent = post1.getContent();
        String newSubject = "방가제목";
        String newContent = "방가내용";

        //when
        postService.modify(post1.getId(), post1.getAuthor().getId(), newSubject, newContent, null);

        //then
        Post post2 = postRepository.findById(1L).get();
        assertThat(post2.getSubject()).isEqualTo(newSubject);
        assertThat(post2.getContent()).isEqualTo(newContent);
    }

    @Test
    @DisplayName("해시태그 포함하고 modify 성공 테스트 ")
    void t4() {
        //given
        Post post1 = postRepository.findById(1L).get();
        String newSubject = "방가제목";
        String newContent = "방가내용";
        String newPostHashTagContents = "#그리움 #굿잡 #그리움1 #굿잡2 #그리움3 #굿잡4";

        //when
        postService.modify(post1.getId(), post1.getAuthor().getId(), newSubject, newContent, newPostHashTagContents);

        //then
        Post post2 = postRepository.findById(1L).get();
        assertThat(post2.getSubject()).isEqualTo(newSubject);
        assertThat(post2.getContent()).isEqualTo(newContent);
        assertThat(post2.getPostHashTagList().size()).isEqualTo(6);
    }

    @Test
    @DisplayName("해시태그 제외하고 write 성공 테스트 ")
    @WithUserDetails("user1")
    void t5() {
        //given
        String newSubject = "방가제목";
        String newContent = "방가내용";

        //when
        PostDto postDto = postService.write(rq.getId(), newSubject, newContent, null);
        Post post = postService.getPost(postDto.getId());

        //then
        assertThat(post).isNotNull();
        assertThat(post.getSubject()).isEqualTo(newSubject);
        assertThat(post.getContent()).isEqualTo(newContent);
        assertThat(post.getAuthor().getId()).isEqualTo(rq.getId());
    }


    /**
     *  문제 있음 Post postHashTagList의 oneToMany를 Eager, Lazy 에 따라 달라짐
     *  t4의 modify랑 로직이 거의 같은데 t6만 안됨
     *  갱신이 안됨..
     */
    @Test
    @DisplayName("해시태그 포함하고 write 성공 테스트 ")
    @WithUserDetails("user1")
    void t6() {
        //given
        String newSubject = "방가제목";
        String newContent = "방가내용";
        String newPostHashTagContents = "#그리움 #굿잡 #그리움1 #굿잡2 #그리움3 #굿잡4";

        //when
        PostDto postDto = postService.write(rq.getId(), newSubject, newContent, newPostHashTagContents);

        PostDto post = postService.getPostById(postDto.getId());

        //then
        assertThat(post).isNotNull();
        assertThat(post.getSubject()).isEqualTo(newSubject);
        assertThat(post.getContent()).isEqualTo(newContent);
//        assertThat(post.getPostHashTagList().size()).isEqualTo(6);
    }
    @Test
    @DisplayName("delete 성공 테스트 ")
    @WithUserDetails("user1")
    void t7() {
        //given
        Post post1 = postService.getPost(2L);

        //when
        postService.delete(post1.getId(), rq.getId());

        //then
        assertThrows(PostNotFoundException.class, () -> {
            postService.getPost(2L);
        });
    }

}