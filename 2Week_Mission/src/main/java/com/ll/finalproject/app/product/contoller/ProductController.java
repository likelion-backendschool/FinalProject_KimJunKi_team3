package com.ll.finalproject.app.product.contoller;

import com.ll.finalproject.app.base.rq.Rq;
import com.ll.finalproject.app.member.service.MemberService;
import com.ll.finalproject.app.post.keyword.entity.PostKeyword;
import com.ll.finalproject.app.post.keyword.service.PostKeywordService;
import com.ll.finalproject.app.post.service.PostService;
import com.ll.finalproject.app.product.dto.ProductDto;
import com.ll.finalproject.app.product.entity.Product;
import com.ll.finalproject.app.product.dto.ProductCreateForm;
import com.ll.finalproject.app.product.dto.ProductModifyForm;
import com.ll.finalproject.app.product.service.ProductService;
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

        List<ProductDto> productDtos = productService.getProductList();

        model.addAttribute("products", productDtos);
        return "product/list";
    }

    @GetMapping("/{productId}")
    public String detail(@PathVariable Long productId, Model model) {

        ProductDto productDto = productService.getProductById(productId);

        model.addAttribute("product", productDto);
        return "product/detail";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/create")
    public String showCreate(Model model) {

        List<PostKeyword> postKeywords = postKeywordService.findByMemberId(rq.getId());

        model.addAttribute("postKeywords", postKeywords);
        model.addAttribute("productCreateForm", new ProductCreateForm());
        return "product/create";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/create")
    public String create(@Valid ProductCreateForm productCreateForm, BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return "product/create";
        }

        Product product = productService.create(rq.getId(), productCreateForm.getSubject(), productCreateForm.getDescription(),
                productCreateForm.getPrice(), productCreateForm.getPostKeywordId());

        return "redirect:/product/%d".formatted(product.getId());
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{productId}/modify")
    public String showModify(@PathVariable Long productId, Model model) {

        ProductDto productDto = productService.getProductById(productId, rq.getId());

        model.addAttribute("product", productDto);
        return "product/modify";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/{productId}/modify")
    public String modify(@PathVariable Long productId, @Valid @ModelAttribute("product") ProductModifyForm productModifyForm) {
        productService.modify(productId, rq.getId(), productModifyForm.getSubject(), productModifyForm.getPrice(), productModifyForm.getDescription());
        return "redirect:/product/%d".formatted(productId);
    }

    @GetMapping("/{productId}/delete")
    public String delete(@PathVariable Long productId) {
        productService.delete(productId, rq.getId());
        return "redirect:/product/list";
    }
}
