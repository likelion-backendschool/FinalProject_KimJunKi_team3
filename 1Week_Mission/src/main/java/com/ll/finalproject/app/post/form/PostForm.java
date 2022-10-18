package com.ll.finalproject.app.post.form;

import lombok.Data;
import javax.validation.constraints.NotBlank;
@Data
public class PostForm {

    @NotBlank(message = "제목 필수항목입니다.")
    private String subject;

    @NotBlank(message = "내용은 필수항목입니다.")
    private String content;

    private String hashTagContents;
}