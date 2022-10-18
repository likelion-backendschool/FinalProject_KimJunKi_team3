package com.ll.finalproject.app.post.keyword.entity;

import com.ll.finalproject.app.base.entity.BaseEntity;
import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.Entity;

@Entity
@Getter
@NoArgsConstructor
@SuperBuilder
@ToString(callSuper = true)
public class PostKeyword extends BaseEntity {
    private String content;

    public String getListUrl() {
        return "/post/keyword/list?kw=%s".formatted(content);
    }
}
