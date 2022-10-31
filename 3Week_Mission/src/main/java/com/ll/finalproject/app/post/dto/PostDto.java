package com.ll.finalproject.app.post.dto;

import com.ll.finalproject.app.post.keyword.entity.PostKeyword;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

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

    public String getExtra_inputValue_hashTagContents() {

        if (postHashTagList == null || postHashTagList.size() == 0) {
            return "";
        }

        return postHashTagList
                .stream()
                .map(postKeyword -> "#" + postKeyword.getContent())
                .sorted()
                .collect(Collectors.joining(" "));
    }
}