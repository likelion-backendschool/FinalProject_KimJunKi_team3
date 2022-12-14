package com.ll.finalproject.app.post.service;

import com.ll.finalproject.app.member.entity.Member;
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
public class PostService {
    private final PostRepository postRepository;
    private final PostHashTagService postHashTagService;
    private final PostKeywordRepository postKeywordRepository;
    private final PostHashTagRepository postHashTagRepository;
    private final CommonUtil commonUtil;

    public List<Post> getLatestPost() {
        return postRepository.findFirst100ByOrderByIdDesc();
    }

    public Post write(Member author, String subject, String content, String hashTagContents) {
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

    public List<Post> getPostListByPostKeyword(Member member, String kw) {
        // member ??? ?????? ??? ?????? kw ????????? ????????? PostHashTag ????????? ??????
        List<PostHashTag> postHashTags = postHashTagRepository.findAllByMemberIdAndPostKeyword_contentOrderByPost_idDesc(member.getId(), kw);

        log.info("postHashTags = {}", postHashTags);

        if (postHashTags == null || postHashTags.size() == 0) {
            return null;
        }
        // PostHashTag ????????? Post??? ?????????
        List<Post> posts = postHashTags.stream()
                .map(PostHashTag::getPost)
                .collect(toList());

        loadForPrintData(posts);
        return posts;
    }
    public Post getPostById(Long id) {
        Post post = findById(id).orElseThrow(
                () -> new PostNotFoundException("?????? ?????? ???????????? ????????????")
        );
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

    public Post modify(long authorId, long postId, String subject, String content, String hashTagContents) {
        Post post = getPostById(postId);
        if (post == null) {
            throw new PostNotFoundException("?????? ?????? ???????????? ????????????.");
        }
        if (post.getAuthor().getId() != authorId) {
            throw new NoAuthorizationException("?????? ?????? ?????? ????????? ????????????.");
        }

        post.changeSubjectAndContent(subject, content);
        postRepository.save(post);

        postHashTagService.applyPostHashTags(post, hashTagContents);

        return post;
    }

    public void delete(Post post) {
        postRepository.delete(post);
    }

    public List<Post> getPostByAuthor(Member member) {
        List<Post> posts = postRepository.findByAuthor(member).orElse(null);
        if (posts == null) {
            return null;
        }
        return posts;
    }
}
