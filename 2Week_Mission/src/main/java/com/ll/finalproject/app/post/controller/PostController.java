package com.ll.finalproject.app.post.controller;

import com.ll.finalproject.app.base.rq.Rq;
import com.ll.finalproject.app.post.dto.PostDto;
import com.ll.finalproject.app.post.dto.PostForm;
import com.ll.finalproject.app.post.exception.NoAuthorizationException;
import com.ll.finalproject.app.post.exception.PostNotFoundException;
import com.ll.finalproject.app.post.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;


@Controller
@RequestMapping("/post")
@RequiredArgsConstructor
@Slf4j
public class PostController {

    private final PostService postService;
    private final Rq rq;

    @GetMapping("/list")
    public String list(Model model) {

        List<PostDto> postDtos = postService.getPostList();

        model.addAttribute("posts", postDtos);
        return "post/list";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/keyword/list")
    public String list(@RequestParam("kw") String kw, Model model) {

        List<PostDto> postDtos = postService.getPostListByPostKeyword(rq.getId(), kw);

        model.addAttribute("posts", postDtos);
        return "post/keyword";
    }

    @GetMapping("/{postId}")
    public String detail(@PathVariable Long postId, Model model) {

        try {
            PostDto postDto = postService.getPostById(postId);
            model.addAttribute("post", postDto);
        } catch (PostNotFoundException e) {
            return "post/list";
        }

        return "post/detail";
    }


    @PreAuthorize("isAuthenticated()")
    @GetMapping("/write")
    public String showWrite(@ModelAttribute PostForm postForm) {
        return "post/write";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/write")
    public String write(@Valid PostForm postForm, BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return "post/write";
        }

        PostDto postDto = postService.write(rq.getId(), postForm.getSubject(), postForm.getContent(), postForm.getHashTagContents());

        return "redirect:/post/%d".formatted(postDto.getId());
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{postId}/modify")
    public String showModify(@PathVariable Long postId, Model model) {

        try {
            PostDto postDto = postService.getPostById(postId, rq.getId());

            model.addAttribute("post", postDto);
        } catch (PostNotFoundException e) {
            return "post/list";
        }

        return "post/modify";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/{postId}/modify")
    public String modify(@PathVariable Long postId, @Valid @ModelAttribute("post") PostForm postForm, BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return "post/modify";
        }

        try {
            postService.modify(postId, rq.getId(), postForm.getSubject(), postForm.getContent(), postForm.getHashTagContents());
        } catch (PostNotFoundException e) {
            return "post/list";
        } catch (NoAuthorizationException e) {
            return "redirect:/post/%d".formatted(postId);
        }

        return "redirect:/post/%d".formatted(postId);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{postId}/delete")
    public String delete(@PathVariable Long postId) {
        postService.delete(postId, rq.getId());
        return "redirect:/post/list";
    }
}
