package com.ll.finalproject.app.home.controller;

import com.ll.finalproject.app.post.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final PostService postService;

    @GetMapping("/")
    public String showMain(Model model) {
        postService.getLatestPost();
        return "home/main";
    }
}