package com.ll.exam.final__2022_10_08.api.myBook.dto;

import com.ll.exam.final__2022_10_08.api.product.dto.ProductResponse;
import com.ll.exam.final__2022_10_08.app.myBook.entity.MyBook;
import com.ll.exam.final__2022_10_08.app.product.entity.Product;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class MyBooksResponse {

    private List<MyBookResponse> myBooks;

    public static MyBooksResponse of(List<MyBookResponse> myBookResponseList) {
        return MyBooksResponse.builder()
                .myBooks(myBookResponseList)
                .build();
    }
}
