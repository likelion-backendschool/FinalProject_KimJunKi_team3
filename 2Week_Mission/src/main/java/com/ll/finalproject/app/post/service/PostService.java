package com.ll.finalproject.app.post.service;

import com.ll.finalproject.app.member.dto.MemberDto;
import com.ll.finalproject.app.member.entity.Member;
import com.ll.finalproject.app.member.service.MemberService;
import com.ll.finalproject.app.post.entity.Post;
import com.ll.finalproject.app.post.exception.NoAuthorizationException;
import com.ll.finalproject.app.post.exception.PostNotFoundException;
import com.ll.finalproject.app.post.hashTag.entity.PostHashTag;
import com.ll.finalproject.app.post.hashTag.repository.PostHashTagRepository;
import com.ll.finalproject.app.post.hashTag.service.PostHashTagService;
import com.ll.finalproject.app.post.keyword.entity.PostKeyword;
import com.ll.finalproject.app.post.keyword.repository.PostKeywordRepository;
import com.ll.finalproject.app.post.repository.PostRepository;
import com.ll.finalproject.app.post.util.CommonUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class PostService {
    private final PostRepository postRepository;
    private final PostHashTagRepository postHashTagRepository;
    private final PostHashTagService postHashTagService;
    private final MemberService memberService;
    private final CommonUtil commonUtil;

    @Transactional(readOnly = true)
    public Post findById(Long postId) {
        return postRepository.findById(postId).orElseThrow(
                () -> new PostNotFoundException("해당 글은 존재하지 않습니다."));
    }

    public List<Post> getLatestPost() {
        return postRepository.findFirst100ByOrderByIdDesc();
    }

    public Post write(Long memberId, String subject, String content, String hashTagContents) {

        Member author = memberService.findById(memberId);

        Post post = Post
                .builder()
                .author(author)
                .subject(subject)
                .content(content)
                .contentHtml(commonUtil.markdown(content))
                .build();

        postRepository.save(post);

        if (hashTagContents != null) {
            postHashTagService.applyPostHashTags(post, hashTagContents);
        }

        return post;
    }

    public List<Post> getPostList() {

        List<Post> posts = postRepository.findAllByOrderByIdDesc();
        loadForPrintData(posts);

        return posts;
    }

    public List<Post> getPostListByPostKeyword(Long memberId , String kw) {
        // member 가 썼던 글 중에 kw 태그가 들어간 PostHashTag 데이터 목록
        List<PostHashTag> postHashTags = postHashTagRepository.findAllByMemberIdAndPostKeyword_contentOrderByPost_idDesc(memberId, kw);

        log.info("postHashTags = {}", postHashTags);

        if (postHashTags == null || postHashTags.size() == 0) {
            return null;
        }
        // PostHashTag 필드의 Post만 가져옴
        List<Post> posts = postHashTags.stream()
                .map(PostHashTag::getPost)
                .collect(toList());

        loadForPrintData(posts);

        return posts;
    }

    public Post getPostById(Long postId) {

        Post post = findById(postId);
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

    public Post modify(Long authorId, Long postId, String subject, String content, String hashTagContents) {

        Post post = getPostById(postId);

        if (post == null) {
            throw new PostNotFoundException("해당 글은 존재하지 않습니다.");
        }

        if (post.getAuthor().getId() != authorId) {
            throw new NoAuthorizationException("해당 글의 수정 권한이 없습니다.");
        }

        post.changeSubjectAndContent(subject, content);
        postRepository.save(post);

        postHashTagService.applyPostHashTags(post, hashTagContents);

        return post;
    }

    public void delete(Post post) {
        postRepository.delete(post);
    }

    public List<Post> getPostByAuthorId(Long memberId) {

        List<Post> posts = postRepository.findByAuthorId(memberId);
        return posts;
    }
}
