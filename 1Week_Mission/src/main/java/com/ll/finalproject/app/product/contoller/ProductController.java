package com.ll.finalproject.app.product.contoller;

import com.ll.finalproject.app.member.entity.Member;
import com.ll.finalproject.app.member.service.MemberService;
import com.ll.finalproject.app.post.entity.Post;
import com.ll.finalproject.app.post.form.PostForm;
import com.ll.finalproject.app.post.keyword.entity.PostKeyword;
import com.ll.finalproject.app.post.keyword.service.PostKeywordService;
import com.ll.finalproject.app.product.entity.Product;
import com.ll.finalproject.app.product.form.PostCreateForm;
import com.ll.finalproject.app.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/product")
@RequiredArgsConstructor
@Slf4j
public class ProductController {

    private final ProductService productService;
    private final MemberService memberService;
    private final PostKeywordService postKeywordService;

    @GetMapping("/list")
    public String list(Model model) {
        List<Product> productList = productService.getProductList();
        model.addAttribute("productList", productList);
        return "product/list";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable("id") Long id, Model model) {

        Product product = productService.getProductById(id);
        if (product == null) {
            return "product/list";
        }
        model.addAttribute("product", product);
        return "product/detail";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/create")
    public String showCreate(Model model) {
        model.addAttribute("postCreateForm", new PostCreateForm());
        return "product/create";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/create")
    public String create(@Valid PostCreateForm postCreateForm, BindingResult bindingResult, Principal principal) {

        if (bindingResult.hasErrors()) {
            return "product/create";
        }

        Member member = memberService.findByUsername(principal.getName()).get();
        PostKeyword postKeyword = postKeywordService.findById(postCreateForm.getPostKeywordId()).orElse(null);
        if (postKeyword == null) {
            bindingResult.rejectValue("postKeywordId",null ,"존재하지 않는 키워드입니다.");
            return "product/create";
        }

        Product product = productService.create(member, postKeyword, postCreateForm.getSubject(), postCreateForm.getDescription(), postCreateForm.getPrice());

        return "redirect:/product/%d".formatted(product.getId());
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{id}/modify")
    public String showModify() {
        return "product/modify";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/{id}/modify")
    public String modify() {
        return "product/modify";
    }

    @GetMapping("/{id}/delete")
    public String delete() {
        return "product/list";
    }
}
