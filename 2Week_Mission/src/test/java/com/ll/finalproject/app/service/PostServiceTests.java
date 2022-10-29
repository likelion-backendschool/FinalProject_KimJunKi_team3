package com.ll.finalproject.app.service;

import com.ll.finalproject.app.base.rq.Rq;
import com.ll.finalproject.app.member.entity.Member;
import com.ll.finalproject.app.member.mapper.MemberMapper;
import com.ll.finalproject.app.member.repository.MemberRepository;
import com.ll.finalproject.app.post.entity.Post;
import com.ll.finalproject.app.post.repository.PostRepository;
import com.ll.finalproject.app.post.service.PostService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles({"test", "base"})
@Transactional
class PostServiceTests {

    @Autowired
    private PostRepository postRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private PostService postService;

    @Test
    @DisplayName("findByMember vs findByMemberId")
    void t1() {
        Member member = memberRepository.findById(1L).get();

        System.out.println("findByAuthor 쿼리문");
        postRepository.findByAuthor(member);

        System.out.println("findByAuthorId 쿼리문");
        postRepository.findByAuthorId(member.getId());
    }
}