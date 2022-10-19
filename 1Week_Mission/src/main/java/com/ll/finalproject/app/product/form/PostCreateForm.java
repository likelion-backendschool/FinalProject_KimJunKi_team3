package com.ll.finalproject.app.product.form;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class PostCreateForm {

    @NotBlank(message = "도서 이름은 필수항목입니다.")
    private String subject;

    @NotBlank(message = "가격은 필수항목입니다.")
    private int price;

    @NotBlank(message = "키워드는 필수항목입니다.")
    private Long postKeywordId;
}