package com.ll.finalproject.app.product.dto;

import com.ll.finalproject.app.member.dto.MemberDto;
import com.ll.finalproject.app.post.keyword.entity.PostKeyword;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductDto {

    private Long id;

    private MemberDto author;

    private PostKeyword postKeyword;

    private String subject;

    private String content;

    private String description;

    private int price;

    private LocalDateTime createDate;

    private LocalDateTime modifyDate;
}
