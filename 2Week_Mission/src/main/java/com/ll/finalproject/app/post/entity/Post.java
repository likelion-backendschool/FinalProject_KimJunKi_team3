package com.ll.finalproject.app.post.entity;

import com.ll.finalproject.app.base.entity.BaseEntity;
import com.ll.finalproject.app.member.entity.Member;
import com.ll.finalproject.app.post.hashTag.entity.PostHashTag;
import com.ll.finalproject.app.post.keyword.entity.PostKeyword;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static javax.persistence.FetchType.EAGER;
import static javax.persistence.FetchType.LAZY;

@Entity
@Getter
@NoArgsConstructor
@SuperBuilder
@ToString(callSuper = true)
public class Post extends BaseEntity {

    @ManyToOne(fetch = LAZY)
    private Member author;
    @NotBlank(message = "제목은 필수항목입니다.")
    private String subject;
    @NotBlank(message = "내용은 필수항목입니다.")
    @Column(columnDefinition = "TEXT")
    private String content;
    @Column(columnDefinition = "TEXT")
    private String contentHtml;
    @OneToMany(mappedBy = "post", cascade = {CascadeType.ALL}, orphanRemoval=true)
    private List<PostHashTag> postHashTagList;

    public List<PostKeyword> getPostKeyWordsFromPostHashTags() {
        if (postHashTagList == null) {
            return null;
        }
        return postHashTagList.stream()
                .map(PostHashTag::getPostKeyword)
                .toList();
    }

    public void changeSubjectAndContent(String subject, String content) {
        this.subject = subject;
        this.content = content;
    }
    public void changePostHashTagList(List<PostHashTag> postHashTags) {
        this.postHashTagList = postHashTags;
    }
    public boolean isAuthor(Member author) {
        return this.author.equals(author);
    }


    // 해시태그 수정 input 형식에 맞게 가공 (아래 로직 안 쓰는 중
    public String getExtra_inputValue_hashTagContents() {
        Map<String, Object> extra = getExtra();

        if (extra.containsKey("postHashTags") == false) {
            return "";
        }

        List<PostHashTag> postHashTags = (List< PostHashTag>) extra.get("postHashTags");

        if (postHashTags.isEmpty()) {
            return "";
        }

        return postHashTags
                .stream()
                .map(postHashTag -> "#" + postHashTag.getPostKeyword().getContent())
                .sorted()
                .collect(Collectors.joining(" "));
    }

    public String getExtra_postHashTagLinks() {
        Map<String, Object> extra = getExtra();

        if (extra.containsKey("postHashTags") == false) {
            return "";
        }

        List<PostHashTag> postHashTags = (List<PostHashTag>) extra.get("postHashTags");

        if (postHashTags.isEmpty()) {
            return "";
        }

        return postHashTags
                .stream()
                .map(postHashTag -> {
                    String text = "#" + postHashTag.getPostKeyword().getContent();

                    return """
                            <a href="%s" >%s</a>
                            """
                            .stripIndent()
                            .formatted(postHashTag.getPostKeyword().getListUrl(), text);
                })
                .sorted()
                .collect(Collectors.joining(" "));
    }


}
