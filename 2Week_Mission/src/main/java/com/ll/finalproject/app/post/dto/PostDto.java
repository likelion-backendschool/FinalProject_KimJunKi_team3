package com.ll.finalproject.app.post.entity;

import com.ll.finalproject.app.base.entity.BaseEntity;
import com.ll.finalproject.app.member.entity.Member;
import com.ll.finalproject.app.post.hashTag.entity.PostHashTag;
import com.ll.finalproject.app.post.keyword.entity.PostKeyword;
import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static javax.persistence.FetchType.LAZY;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PostDto {

    private Long id;

    private String author;

    private String subject;

    private String content;

    private String contentHtml;

    private LocalDateTime createDate;

    private LocalDateTime modifyDate;

    private List<PostKeyword> postHashTagList;
}
