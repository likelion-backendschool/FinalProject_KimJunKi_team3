package com.ll.finalproject.app.post.hashTag.repository;

import com.ll.finalproject.app.post.hashTag.entity.PostHashTag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PostHashTagRepository extends JpaRepository<PostHashTag, Long> {
    Optional<PostHashTag> findByPostIdAndPostKeywordId(Long postId, Long postKeywordId);

    List<PostHashTag> findAllByPostId(Long postId);

    List<PostHashTag> findAllByPostIdIn(long[] ids);
}