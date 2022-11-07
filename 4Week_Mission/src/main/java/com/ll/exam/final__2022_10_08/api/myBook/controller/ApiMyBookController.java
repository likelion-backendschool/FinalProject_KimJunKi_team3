package com.ll.exam.final__2022_10_08.api.myBook.controller;

import com.ll.exam.final__2022_10_08.api.myBook.dto.MyBookResponse;
import com.ll.exam.final__2022_10_08.api.myBook.dto.MyBooksResponse;
import com.ll.exam.final__2022_10_08.app.base.dto.RsData;
import com.ll.exam.final__2022_10_08.app.myBook.entity.MyBook;
import com.ll.exam.final__2022_10_08.app.myBook.service.MyBookService;
import com.ll.exam.final__2022_10_08.app.security.dto.MemberContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/myBooks")
public class ApiMyBookController {

    private final MyBookService myBookService;
    @GetMapping("/")
    public ResponseEntity<RsData> showMyBooks(@AuthenticationPrincipal MemberContext memberContext) {
        MyBooksResponse myBooks = myBookService.getMyBooks(memberContext.getId());

        return ResponseEntity
                .ok()
                .body(RsData.of(
                        "S-1",
                        "성공",
                        myBooks
                ));
    }

    @GetMapping("/{myBookId}")
    public ResponseEntity<RsData> showMyBooks(@AuthenticationPrincipal MemberContext memberContext,
                                              @PathVariable Long myBookId) {

        myBookService.getMyBook(myBookId, memberContext.getId());

        return ResponseEntity
                .ok()
                .body(RsData.of(
                        "S-1",
                        "성공"
                ));
    }
}
