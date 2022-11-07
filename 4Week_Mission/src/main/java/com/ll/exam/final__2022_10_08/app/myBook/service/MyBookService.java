package com.ll.exam.final__2022_10_08.app.myBook.service;

import com.ll.exam.final__2022_10_08.api.common.exception.ExceptionType;
import com.ll.exam.final__2022_10_08.api.common.exception.MyBookInvalidException;
import com.ll.exam.final__2022_10_08.api.myBook.dto.MyBookResponse;
import com.ll.exam.final__2022_10_08.api.myBook.dto.MyBooksResponse;
import com.ll.exam.final__2022_10_08.app.base.dto.RsData;
import com.ll.exam.final__2022_10_08.app.myBook.entity.MyBook;
import com.ll.exam.final__2022_10_08.app.myBook.repository.MyBookRepository;
import com.ll.exam.final__2022_10_08.app.order.entity.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MyBookService {
    private final MyBookRepository myBookRepository;

    @Transactional
    public RsData add(Order order) {
        order.getOrderItems()
                .stream()
                .map(orderItem -> MyBook.builder()
                        .owner(order.getBuyer())
                        .orderItem(orderItem)
                        .product(orderItem.getProduct())
                        .build())
                .forEach(myBookRepository::save);

        return RsData.of("S-1", "나의 책장에 추가되었습니다.");
    }

    @Transactional
    public RsData remove(Order order) {
        order.getOrderItems()
                .stream()
                .forEach(orderItem -> myBookRepository.deleteByProductIdAndOwnerId(orderItem.getProduct().getId(), order.getBuyer().getId()));

        return RsData.of("S-1", "나의 책장에서 제거되었습니다.");
    }

    public MyBooksResponse getMyBooks(Long id) {
        return MyBooksResponse.of
                (
                        myBookRepository.findAllByOwnerId(id)
                        .stream()
                        .map(MyBookResponse::of)
                        .collect(toList())
                );
    }

    public void getMyBook(Long myBookId, Long memberId) {
        MyBook myBook = myBookRepository.findById(myBookId).orElseThrow(
                () -> new MyBookInvalidException(ExceptionType.MYBOOK_NOT_FOUND, myBookId)
        );

        if (!myBookRepository.existsByIdAndOwnerId(myBookId, memberId)) {
            throw new MyBookInvalidException(ExceptionType.INVALID_MYBOOK_OWNER);
        }

    }
}
