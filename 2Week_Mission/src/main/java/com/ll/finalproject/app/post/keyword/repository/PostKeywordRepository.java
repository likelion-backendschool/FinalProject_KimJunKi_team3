package com.ll.finalproject.app.post.keyword.repository;

import com.ll.finalproject.app.post.keyword.entity.PostKeyword;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PostKeywordRepository extends JpaRepository<PostKeyword, Long> {
    Optional<PostKeyword> findByContent(String keywordContent);
}