package com.ll.finalproject.app.post.hashTag.service;

import com.ll.finalproject.app.post.entity.Post;
import com.ll.finalproject.app.post.hashTag.entity.PostHashTag;
import com.ll.finalproject.app.post.hashTag.repository.PostHashTagRepository;
import com.ll.finalproject.app.post.keyword.entity.PostKeyword;
import com.ll.finalproject.app.post.keyword.service.PostKeywordService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostHashTagService {
    private final PostKeywordService postKeywordService;
    private final PostHashTagRepository postHashTagRepository;

    public void applyPostHashTags(Post post, String hashTagContents) {
        List<PostHashTag> oldPostHashTags = getPostHashTags(post);

        List<String> keywordContents = Arrays.stream(hashTagContents.split("#"))
                .map(String::trim)
                .filter(s -> s.length() > 0)
                .collect(Collectors.toList());

        List<PostHashTag> needToDelete = new ArrayList<>();

        for (PostHashTag oldPostHashTag : oldPostHashTags) {
            boolean contains = keywordContents.stream().anyMatch(s -> s.equals(oldPostHashTag.getPostKeyword().getContent()));

            if (contains == false) {
                needToDelete.add(oldPostHashTag);
            }
        }

        needToDelete.forEach(hashTag -> {
            postHashTagRepository.delete(hashTag);
        });

        keywordContents.forEach(keywordContent -> {
            saveHashTag(post, keywordContent);
        });
    }

    private PostHashTag saveHashTag(Post post, String keywordContent) {
        PostKeyword postKeyword = postKeywordService.save(keywordContent);

        Optional<PostHashTag> opPostHashTag = postHashTagRepository.findByPostIdAndPostKeywordId(post.getId(), postKeyword.getId());

        if (opPostHashTag.isPresent()) {
            return opPostHashTag.get();
        }

        PostHashTag postHashTag = PostHashTag.builder()
                .post(post)
                .postKeyword(postKeyword)
                .build();

        postHashTagRepository.save(postHashTag);

        return postHashTag;
    }

    public List<PostHashTag> getPostHashTags(Post post) {
        return postHashTagRepository.findAllByPostId(post.getId());
    }

    public List<PostHashTag> getPostHashTagsByPostIdIn(long[] ids) {
        return postHashTagRepository.findAllByPostIdIn(ids);
    }
}