package com.ll.exam.final__2022_10_08.api.post.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.ll.exam.final__2022_10_08.app.post.entity.Post;
import com.ll.exam.final__2022_10_08.app.post.repository.PostRepository;
import com.ll.exam.final__2022_10_08.app.product.entity.Product;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostResponse {

    private Long id;
    private String subject;
    private String content;
    private String contentHtml;

    public static PostResponse of(Post post) {
        return PostResponse.builder()
                .id(post.getId())
                .subject(post.getSubject())
                .content(post.getContent())
                .contentHtml(post.getContentHtml())
                .build();
    }
}