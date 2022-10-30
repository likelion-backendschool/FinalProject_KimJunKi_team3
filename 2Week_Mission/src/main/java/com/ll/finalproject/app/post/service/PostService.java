package com.ll.finalproject.app.post.service;

import com.ll.finalproject.app.member.dto.MemberDto;
import com.ll.finalproject.app.member.entity.Member;
import com.ll.finalproject.app.member.exception.MemberNotFoundException;
import com.ll.finalproject.app.member.repository.MemberRepository;
import com.ll.finalproject.app.member.service.MemberService;
import com.ll.finalproject.app.post.entity.Post;
import com.ll.finalproject.app.post.entity.PostDto;
import com.ll.finalproject.app.post.exception.NoAuthorizationException;
import com.ll.finalproject.app.post.exception.PostNotFoundException;
import com.ll.finalproject.app.post.hashTag.entity.PostHashTag;
import com.ll.finalproject.app.post.hashTag.repository.PostHashTagRepository;
import com.ll.finalproject.app.post.hashTag.service.PostHashTagService;

import com.ll.finalproject.app.post.mapper.PostMapper;
import com.ll.finalproject.app.post.repository.PostRepository;
import com.ll.finalproject.app.post.util.CommonUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;


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
    private final MemberRepository memberRepository;
    private final CommonUtil commonUtil;

    @Transactional(readOnly = true)
    public Post getPost(Long postId) {
        return postRepository.findById(postId).orElseThrow(
                () -> new PostNotFoundException("해당 글은 존재하지 않습니다."));
    }

    @Transactional(readOnly = true)
    public Member getAuthor(Long authorId) {
        return memberRepository.findById(authorId).orElseThrow(
                () -> new MemberNotFoundException("존재하지 않는 회원입니다."));
    }

    public List<Post> getLatestPost() {
        return postRepository.findFirst100ByOrderByIdDesc();
    }

    public List<PostDto> getPostList() {
        List<Post> posts = postRepository.findAllByOrderByIdDesc();
//        loadForPrintData(posts);
        return PostMapper.INSTANCE.entitiesToPostDtos(posts);
    }

    public List<PostDto> getPostListByPostKeyword(Long memberId , String kw) {
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

//        loadForPrintData(posts);

        return PostMapper.INSTANCE.entitiesToPostDtos(posts);
    }

    public PostDto getPostById(Long postId) {
        Post post = getPost(postId);
//        loadForPrintData(post);
        return PostMapper.INSTANCE.entityToPostDto(post);
    }

    public PostDto getPostById(Long postId, Long authorId) {

        Post post = getPost(postId);
        Member author = getAuthor(authorId);

        validateAuthor(post, author);

        return PostMapper.INSTANCE.entityToPostDto(post);
    }

    public PostDto write(Long memberId, String subject, String content, String postHashTagContents) {

        Member author = getAuthor(memberId);

        Post post = Post
                .builder()
                .author(author)
                .subject(subject)
                .content(content)
                .contentHtml(commonUtil.markdown(content))
                .build();

        postRepository.save(post);

        if (postHashTagContents != null) {
            postHashTagService.applyPostHashTags(post, postHashTagContents);
        }

        return PostMapper.INSTANCE.entityToPostDto(post);
    }

    public PostDto modify(Long postId, Long authorId, String subject, String content, String postHashTagContents) {

        Post post = getPost(postId);
        Member author = getAuthor(authorId);

        validateAuthor(post, author);

        post.changeSubjectAndContent(subject, content);

        if (postHashTagContents != null) {
            postHashTagService.applyPostHashTags(post, postHashTagContents);
        }

        return PostMapper.INSTANCE.entityToPostDto(post);
    }

    public void delete(Long postId, Long authorId) {

        Post post = getPost(postId);
        Member author = getAuthor(authorId);

        validateAuthor(post, author);

        postRepository.delete(post);
    }

    private void validateAuthor(Post post, Member author) {
        if (!post.isAuthor(author)) {
            throw new NoAuthorizationException("해당 글의 수정 권한이 없습니다.");
        }
    }

    // product
    public List<PostDto> getPostByAuthorId(Long authorId) {
        List<Post> posts = postRepository.findByAuthorId(authorId);
        return PostMapper.INSTANCE.entitiesToPostDtos(posts);
    }

    /*
        아래 로직은 현재 안 쓰는 중
     */
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
}
