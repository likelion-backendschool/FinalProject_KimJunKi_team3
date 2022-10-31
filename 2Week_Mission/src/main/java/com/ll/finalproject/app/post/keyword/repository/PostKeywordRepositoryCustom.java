package com.ll.finalproject.app.post.keyword.repository;

import com.ll.finalproject.app.post.keyword.entity.PostKeyword;

import java.util.List;

public interface PostKeywordRepositoryCustom {
    List<PostKeyword> getQslAllByAuthorId(Long authorId);
}