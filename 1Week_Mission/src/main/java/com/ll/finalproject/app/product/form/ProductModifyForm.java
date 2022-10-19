package com.ll.finalproject.app.product.form;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class ProductModifyForm {

    @NotBlank(message = "도서 이름은 필수항목입니다.")
    private String subject;

    @NotNull(message = "가격은 필수항목입니다.")
    private Integer price;
    @NotBlank(message = "도서 설명은 필수항목입니다.")
    private String description;

}