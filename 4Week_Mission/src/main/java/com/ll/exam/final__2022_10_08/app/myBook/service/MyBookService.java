package com.ll.exam.final__2022_10_08.app.myBook.service;

import com.ll.exam.final__2022_10_08.api.common.exception.ExceptionType;
import com.ll.exam.final__2022_10_08.api.common.exception.MyBookInvalidException;
import com.ll.exam.final__2022_10_08.api.myBook.dto.MyBookResponse;
import com.ll.exam.final__2022_10_08.api.myBook.dto.MyBooksResponse;
import com.ll.exam.final__2022_10_08.api.post.dto.PostResponse;
import com.ll.exam.final__2022_10_08.api.product.dto.ProductResponse;
import com.ll.exam.final__2022_10_08.app.base.dto.RsData;
import com.ll.exam.final__2022_10_08.app.myBook.entity.MyBook;
import com.ll.exam.final__2022_10_08.app.myBook.repository.MyBookRepository;
import com.ll.exam.final__2022_10_08.app.order.entity.Order;
import com.ll.exam.final__2022_10_08.app.postTag.entity.PostTag;
import com.ll.exam.final__2022_10_08.app.postTag.repository.PostTagRepository;
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
    private final PostTagRepository postTagRepository;

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

    public MyBooksResponse getMyBooksResponse(Long id) {
        return MyBooksResponse.of
                (
                        myBookRepository.findAllByOwnerId(id)
                        .stream()
                        .map(MyBookResponse::of)
                        .collect(toList())
                );
    }

    public MyBookResponse getMyBookResponse(Long myBookId, Long memberId) {

        MyBook myBook = myBookRepository.findById(myBookId).orElseThrow(
                () -> new MyBookInvalidException(ExceptionType.MYBOOK_NOT_FOUND, myBookId)
        );

        if (!myBookRepository.existsByIdAndOwnerId(myBookId, memberId)) {
            throw new MyBookInvalidException(ExceptionType.INVALID_MYBOOK_OWNER);
        }
        /**
         *  도서의 post 태그 정보와 동일한 post 목록을 가져와야 된다.
         *  1. 도서가 가진 Product의 PostKeyword를 가진 PostTag 리스트를 가져온다.
         *  2. PostTag 리스트에서 Post 리스트를 추출한 다음 응답 형태로 가공해준다.
         */
        List<PostResponse> bookChapter = getBookChapter(myBook);

        ProductResponse productResponse = ProductResponse.of(myBook.getProduct(), bookChapter);

        return MyBookResponse.of(myBook, productResponse);

    }

    public List<PostResponse> getBookChapter(MyBook myBook) {

        List<PostTag> postTags = postTagRepository.findAllByPostKeyword(myBook.getProduct().getPostKeyword());

        List<PostResponse> bookChapter = postTags.stream()
                .map((postTag) -> PostResponse.of(postTag.getPost()))
                .collect(toList());

        return bookChapter;
    }
}
