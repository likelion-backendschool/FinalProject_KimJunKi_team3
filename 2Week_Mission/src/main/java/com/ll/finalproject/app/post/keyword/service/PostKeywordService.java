package com.ll.finalproject.app.post.keyword.service;

import com.ll.finalproject.app.post.keyword.entity.PostKeyword;
import com.ll.finalproject.app.post.keyword.repository.PostKeywordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PostKeywordService {
    private final PostKeywordRepository keywordRepository;

    public PostKeyword save(String keywordContent) {
        Optional<PostKeyword> optKeyword = keywordRepository.findByContent(keywordContent);

        if ( optKeyword.isPresent() ) {
            return optKeyword.get();
        }

        PostKeyword postKeyword = PostKeyword
                .builder()
                .content(keywordContent)
                .build();

        keywordRepository.save(postKeyword);

        return postKeyword;
    }

    public Optional<PostKeyword> findById(Long id) {
        return keywordRepository.findById(id);
    }
    public Optional<PostKeyword> findByContent(String content) {
        return keywordRepository.findByContent(content);
    }
}