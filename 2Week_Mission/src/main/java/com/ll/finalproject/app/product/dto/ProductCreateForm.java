package com.ll.finalproject.app.product.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class ProductCreateForm {

    @NotBlank(message = "도서 이름은 필수항목입니다.")
    private String subject;

    @NotBlank(message = "도서 설명은 필수항목입니다.")
    private String description;

    @NotNull(message = "가격은 필수항목입니다.")
    private Integer price;

    @NotNull(message = "키워드는 필수항목입니다.")
    private Long postKeywordId;
}