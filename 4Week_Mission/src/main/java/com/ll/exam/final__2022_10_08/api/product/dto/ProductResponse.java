package com.ll.exam.final__2022_10_08.api.product.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.ll.exam.final__2022_10_08.api.post.dto.PostResponse;
import com.ll.exam.final__2022_10_08.app.myBook.entity.MyBook;
import com.ll.exam.final__2022_10_08.app.post.entity.Post;
import com.ll.exam.final__2022_10_08.app.product.entity.Product;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponse {

    private Long id;
    private LocalDateTime createDate;
    private LocalDateTime modifyDate;
    private Long authorId;
    private String authorName;
    private String subject;
    @JsonInclude(JsonInclude.Include.NON_NULL) // null 일 데이터 안 넘김
    private List<PostResponse> bookChapters;

    public static ProductResponse of(Product product) {

        return ProductResponse.builder()
                .id(product.getId())
                .createDate(product.getCreateDate())
                .modifyDate(product.getModifyDate())
                .authorId(product.getAuthor().getId())
                .authorName(product.getAuthor().getName())
                .subject(product.getSubject())
                .build();
    }
    public static ProductResponse of(Product product, List<PostResponse> bookChapters) {

        return ProductResponse.builder()
                .id(product.getId())
                .createDate(product.getCreateDate())
                .modifyDate(product.getModifyDate())
                .authorId(product.getAuthor().getId())
                .authorName(product.getAuthor().getName())
                .subject(product.getSubject())
                .bookChapters(bookChapters)
                .build();
    }
}