package com.ll.finalproject.app.service;

import com.ll.finalproject.app.post.keyword.entity.PostKeyword;
import com.ll.finalproject.app.post.keyword.service.PostKeywordService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@ActiveProfiles({"test", "base"})
class PostKeywordServiceTest {

    @Autowired
    private PostKeywordService postKeywordService;

    @Test
    @DisplayName("findByMemberId의 데이터 잘 가져오는지 테스트")
    void t1() {
        // given
        PostKeyword postKeyword1 = postKeywordService.findByContent("그리움").get();
        PostKeyword postKeyword2 = postKeywordService.findByContent("행복").get();
        PostKeyword postKeyword3 = postKeywordService.findByContent("슬픔").get();
        PostKeyword postKeyword4 = postKeywordService.findByContent("역경").get();

        // when
        List<PostKeyword> postKeywords = postKeywordService.findByMemberId(1);

        // then
        assertThat(postKeywords).contains(postKeyword1);
        assertThat(postKeywords).contains(postKeyword2);
        assertThat(postKeywords).contains(postKeyword3);
        assertThat(postKeywords).contains(postKeyword4);
    }
}