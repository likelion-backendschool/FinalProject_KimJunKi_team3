package com.ll.finalproject.app.post.service;

import com.ll.finalproject.app.member.entity.Member;
import com.ll.finalproject.app.post.entity.Post;
import com.ll.finalproject.app.post.hashTag.entity.PostHashTag;
import com.ll.finalproject.app.post.hashTag.service.PostHashTagService;
import com.ll.finalproject.app.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostService {
    private final PostRepository postRepository;
    private final PostHashTagService postHashTagService;

    public List<Post> getLatestPost() {
        return postRepository.findFirst100ByOrderByIdDesc();
    }

    public Post write(Member author, String subject, String content, String hashTagContents) {
        Post post = Post
                .builder()
                .author(author)
                .subject(subject)
                .content(content)
                .build();

        postRepository.save(post);

        if (hashTagContents != null) {
            postHashTagService.applyPostHashTags(post, hashTagContents);
        }

        return post;
    }

    public List<Post> getPostList() {
        return postRepository.findAll();
    }

    public Post getPostById(Long id) {
        Post post = findById(id).orElse(null);
        List<PostHashTag> postHashTags = postHashTagService.getPostHashTags(post);
        post.getExtra().put("postHashTags", postHashTags);

        return post;
    }
    @Transactional(readOnly = true)
    public Optional<Post> findById(Long id) {
        return postRepository.findById(id);
    }
}
