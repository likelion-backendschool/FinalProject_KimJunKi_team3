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
import java.util.Map;
import java.util.Optional;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

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
        loadForPrintData(post);

        return post;
    }
    public void loadForPrintData(Post post) {
        List<PostHashTag> postHashTags = postHashTagService.getPostHashTags(post);
        post.getExtra().put("postHashTags", postHashTags);
    }
    public void loadForPrintData(List<Post> posts) {
        long[] ids = posts
                .stream()
                .mapToLong(Post::getId)
                .toArray();

        List<PostHashTag> postHashTagsByPostIds = postHashTagService.getPostHashTagsByPostIdIn(ids);

        Map<Long, List<PostHashTag>> postHashTagsByPostIdsMap = postHashTagsByPostIds.stream()
                .collect(groupingBy(
                        postHashTag -> postHashTag.getPost().getId(), toList()
                ));

        posts.stream().forEach(post -> {
            List<PostHashTag> postHashTags = postHashTagsByPostIdsMap.get(post.getId());

            if (postHashTags == null || postHashTags.size() == 0) return;

            post.getExtra().put("postHashTags", postHashTags);
        });

        log.debug("posts : " + posts);
    }
    @Transactional(readOnly = true)
    public Optional<Post> findById(Long id) {
        return postRepository.findById(id);
    }

    public void modify(Post post, String subject, String content, String hashTagContents) {
        post.changeSubjectAndContent(subject, content);
        postRepository.save(post);

        postHashTagService.applyPostHashTags(post, hashTagContents);
    }

    public void delete(Post post) {
        postRepository.delete(post);
    }
}
