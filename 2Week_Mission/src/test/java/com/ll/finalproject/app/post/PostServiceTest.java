package com.ll.finalproject.app.post;

import com.ll.finalproject.app.post.entity.Post;
import com.ll.finalproject.app.post.service.PostService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles({"test", "base"})
class PostServiceTest {

    @Autowired
    private PostService postService;

    @Test
    @DisplayName("최신글 100개 불러오기")
    void t1() {
        List<Post> posts = postService.getLatestPost();
        for (Post post : posts) {
            System.out.println("post = " + post);
        }
    }

}