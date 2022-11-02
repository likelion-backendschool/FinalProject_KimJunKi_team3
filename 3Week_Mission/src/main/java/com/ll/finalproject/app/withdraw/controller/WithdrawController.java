package com.ll.finalproject.app.withdraw.controller;

import com.ll.finalproject.app.base.rq.Rq;
import com.ll.finalproject.app.member.dto.MemberDto;
import com.ll.finalproject.app.member.service.MemberService;
import com.ll.finalproject.app.withdraw.dto.WithdrawForm;
import com.ll.finalproject.app.withdraw.service.WithdrawService;
import com.ll.finalproject.util.Ut;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/withdraw")
public class WithdrawController {

    private final WithdrawService withdrawService;
    private final MemberService memberService;

    private final Rq rq;
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/apply")
    public String showWithdrawApply(@ModelAttribute WithdrawForm withdrawForm, Model model) {
        MemberDto memberDto = memberService.getMemberDto(rq.getId());
        model.addAttribute("restCash", memberDto.getRestCash());
        return "withdraw/apply";
    }
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/apply")
    public String withdrawApply(@Validated WithdrawForm withdrawForm, BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return "withdraw/apply";
        }

        withdrawService.apply(rq.getId(), withdrawForm.getBankName(), withdrawForm.getBankAccountNo(), withdrawForm.getPrice());

        return "redirect:/withdraw/apply/?msg=%s".formatted(Ut.url.encode("출금 신청이 완료되었습니다."));
    }
}
