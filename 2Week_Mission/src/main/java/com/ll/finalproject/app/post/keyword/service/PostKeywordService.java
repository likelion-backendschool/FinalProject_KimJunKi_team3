package com.ll.finalproject.app.post.keyword.service;

import com.ll.finalproject.app.post.keyword.entity.PostKeyword;
import com.ll.finalproject.app.post.keyword.repository.PostKeywordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class PostKeywordService {
    private final PostKeywordRepository postKeywordRepository;

    public PostKeyword save(String keywordContent) {
        Optional<PostKeyword> optKeyword = postKeywordRepository.findByContent(keywordContent);

        if ( optKeyword.isPresent() ) {
            return optKeyword.get();
        }

        PostKeyword postKeyword = PostKeyword
                .builder()
                .content(keywordContent)
                .build();

        postKeywordRepository.save(postKeyword);

        return postKeyword;
    }

    public Optional<PostKeyword> findById(Long id) {
        return postKeywordRepository.findById(id);
    }
    public Optional<PostKeyword> findByContent(String content) {
        return postKeywordRepository.findByContent(content);
    }

    public List<PostKeyword> findByMemberId(long authorId) {
        return postKeywordRepository.getQslAllByAuthorId(authorId);
    }
}