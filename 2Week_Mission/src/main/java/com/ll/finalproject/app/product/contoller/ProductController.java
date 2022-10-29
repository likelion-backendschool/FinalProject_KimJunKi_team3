package com.ll.finalproject.app.product.contoller;

import com.ll.finalproject.app.base.rq.Rq;
import com.ll.finalproject.app.member.entity.Member;
import com.ll.finalproject.app.member.service.MemberService;
import com.ll.finalproject.app.post.entity.Post;
import com.ll.finalproject.app.post.hashTag.entity.PostHashTag;
import com.ll.finalproject.app.post.keyword.entity.PostKeyword;
import com.ll.finalproject.app.post.keyword.service.PostKeywordService;
import com.ll.finalproject.app.post.service.PostService;
import com.ll.finalproject.app.product.entity.Product;
import com.ll.finalproject.app.product.form.ProductCreateForm;
import com.ll.finalproject.app.product.form.ProductModifyForm;
import com.ll.finalproject.app.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Controller
@RequestMapping("/product")
@RequiredArgsConstructor
@Slf4j
public class ProductController {

    private final ProductService productService;
    private final MemberService memberService;
    private final PostKeywordService postKeywordService;
    private final PostService postService;
    private final Rq rq;

    @GetMapping("/list")
    public String list(Model model) {

        List<Product> productList = productService.getProductList();

        model.addAttribute("productList", productList);
        return "product/list";
    }

    @GetMapping("/{productId}")
    public String detail(@PathVariable Long productId, Model model) {

        Product product = productService.getProductById(productId);

        if (product == null) {
            return "product/list";
        }

        model.addAttribute("product", product);
        return "product/detail";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/create")
    public String showCreate(Model model) {

        List<Post> postList = postService.getPostByAuthorId(rq.getId());

        Set<PostKeyword> postKeywordSet = new HashSet<>();
        for (Post post : postList) {
            for (PostHashTag postHashTag : post.getPostHashTagList()) {
                postKeywordSet.add(postHashTag.getPostKeyword());
            }
        }

        model.addAttribute("postKeywordSet", postKeywordSet);
        model.addAttribute("productCreateForm", new ProductCreateForm());
        return "product/create";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/create")
    public String create(@Valid ProductCreateForm postCreateForm, BindingResult bindingResult) {
        log.info("rq.getId() = {}", rq.getId());
        log.info("rq.getId() = {}", rq.getMemberDto());
        if (bindingResult.hasErrors()) {
            return "product/create";
        }

        PostKeyword postKeyword = postKeywordService.findById(postCreateForm.getPostKeywordId()).orElse(null);

        if (postKeyword == null) {
            bindingResult.rejectValue("postKeywordId",null ,"존재하지 않는 키워드입니다.");
            return "product/create";
        }
        // content 생성
        List<Post> postList = postService.getPostByAuthorId(rq.getId());
        StringBuffer sb = new StringBuffer();
        for (Post post : postList) {
            for (PostHashTag postHashTag : post.getPostHashTagList()) {
                if (postHashTag.getPostKeyword().getId().equals(postCreateForm.getPostKeywordId())) {
                    sb.append(post.getContent() + "\n");
                    break;
                }
            }
        }

        Product product = productService.create(rq.getId(), postKeyword, postCreateForm.getSubject(),
                sb.toString(), postCreateForm.getDescription(), postCreateForm.getPrice());

        return "redirect:/product/%d".formatted(product.getId());
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{productId}/modify")
    public String showModify(@PathVariable Long productId, Model model) {

        Product product = productService.getProductById(productId);

        if (!product.getAuthor().getId().equals(rq.getId())) {
            return "redirect:/product/%d".formatted(product.getId());
        }

        model.addAttribute("product", product);
        return "product/modify";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/{productId}/modify")
    public String modify(@PathVariable Long productId, @Valid @ModelAttribute("product") ProductModifyForm productModifyForm) {

        Product product = productService.getProductById(productId);

        if (!product.getAuthor().getId().equals(rq.getId())) {
            return "redirect:/product/%d".formatted(product.getId());
        }

        productService.modify(product, productModifyForm.getSubject(), productModifyForm.getPrice(), productModifyForm.getDescription());

        return "redirect:/product/%d".formatted(product.getId());
    }

    @GetMapping("/{productId}/delete")
    public String delete(@PathVariable Long productId) {

        Product product = productService.getProductById(productId);

        if (!product.getAuthor().getId().equals(rq.getId())) {
            return "redirect:/product/%d".formatted(product.getId());
        }
        productService.delete(product);

        return "redirect:/product/list";
    }
}
