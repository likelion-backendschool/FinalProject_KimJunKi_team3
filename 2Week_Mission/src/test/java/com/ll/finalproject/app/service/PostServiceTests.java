package com.ll.finalproject.app.service;

import com.ll.finalproject.app.post.entity.Post;
import com.ll.finalproject.app.post.service.PostService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

@SpringBootTest
@ActiveProfiles({"test", "base"})
class PostServiceTests {

    @Autowired
    private PostService postService;


}