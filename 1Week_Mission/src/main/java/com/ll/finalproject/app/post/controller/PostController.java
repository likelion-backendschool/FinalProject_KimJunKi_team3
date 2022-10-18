package com.ll.finalproject.app.post.controller;

import com.ll.finalproject.app.member.entity.Member;
import com.ll.finalproject.app.member.service.MemberService;
import com.ll.finalproject.app.post.entity.Post;
import com.ll.finalproject.app.post.form.PostForm;
import com.ll.finalproject.app.post.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;


@Controller
@RequestMapping("/post")
@RequiredArgsConstructor
@Slf4j
public class PostController {

    private final PostService postService;
    private final MemberService memberService;


    @GetMapping("/list")
    public String list(Model model) {
        List<Post> postList = postService.getPostList();

        postService.loadForPrintData(postList);

        model.addAttribute("postList", postList);
        return "post/list";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/keyword/list")
    public String list(@RequestParam("kw") String kw, Principal principal, Model model) {

        Member member = memberService.findByUsername(principal.getName()).get();
        List<Post> postList = postService.getPostListByPostKeyword(member, kw);
        if (postList == null) {
            return "post/keyword";
        }
        postService.loadForPrintData(postList);

        model.addAttribute("postList", postList);
        return "post/keyword";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable("id") Long id, Model model) {

        Post post = postService.getPostById(id);
        if (post == null) {
            return "post/list";
        }
        model.addAttribute("post", post);
        return "post/detail";
    }


    @PreAuthorize("isAuthenticated()")
    @GetMapping("/write")
    public String showWrite(Model model) {
        model.addAttribute("postForm", new PostForm());
        return "post/write";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/write")
    public String write(@Valid PostForm postForm, BindingResult bindingResult, Principal principal) {

        if (bindingResult.hasErrors()) {
            return "post/write";
        }

        Member member = memberService.findByUsername(principal.getName()).get();
        Post post = postService.write(member, postForm.getSubject(), postForm.getContent(), postForm.getHashTagContents());

        return "redirect:/post/%d".formatted(post.getId());
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{id}/modify")
    public String showModify(@PathVariable Long id, Model model, Principal principal) {

        Post post = postService.getPostById(id);

        if (!post.getAuthor().getUsername().equals(principal.getName())) {
            return "redirect:/post/%d".formatted(post.getId());
        }

        model.addAttribute("post", post);

        return "post/modify";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/{id}/modify")
    public String modify(@PathVariable Long id, Principal principal, @Valid @ModelAttribute("post") PostForm postForm) {

        Post post = postService.getPostById(id);

        if (!post.getAuthor().getUsername().equals(principal.getName())) {
            return "redirect:/post/%d".formatted(post.getId());
        }

        postService.modify(post, postForm.getSubject(), postForm.getContent(), postForm.getHashTagContents());

        return "redirect:/post/%d".formatted(post.getId());
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{id}/delete")
    public String delete(@PathVariable Long id, Principal principal) {

        Post post = postService.getPostById(id);

        if (!post.getAuthor().getUsername().equals(principal.getName())) {
            return "redirect:/post/%d".formatted(post.getId());
        }

        postService.delete(post);
        return "redirect:/post/list";
    }
}
