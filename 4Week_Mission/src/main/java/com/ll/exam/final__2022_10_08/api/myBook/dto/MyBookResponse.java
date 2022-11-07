package com.ll.exam.final__2022_10_08.api.myBook.dto;

import com.ll.exam.final__2022_10_08.api.product.dto.ProductResponse;
import com.ll.exam.final__2022_10_08.app.myBook.entity.MyBook;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MyBookResponse {

    private Long id;
    private LocalDateTime createDate;
    private LocalDateTime modifyDate;
    private Long ownerId;
    private ProductResponse product;

    public static MyBookResponse of(MyBook myBook) {

        ProductResponse productResponse = ProductResponse.of(myBook.getProduct());

        return MyBookResponse.builder()
                .id(myBook.getId())
                .createDate(myBook.getCreateDate())
                .modifyDate(myBook.getModifyDate())
                .ownerId(myBook.getOwner().getId())
                .product(productResponse)
                .build();
    }
}
