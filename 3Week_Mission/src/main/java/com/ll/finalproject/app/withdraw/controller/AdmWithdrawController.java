package com.ll.finalproject.app.withdraw.controller;

import com.ll.finalproject.app.base.rq.Rq;
import com.ll.finalproject.app.withdraw.entity.Withdraw;
import com.ll.finalproject.app.withdraw.service.WithdrawService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/adm/withdraw")
public class AdmWithdrawController {

    private final WithdrawService withdrawService;
    private final Rq rq;

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/applyList")
    public String showWithdrawApply(Model model) {
        List<Withdraw> withdraws = withdrawService.getWithDraw();
        model.addAttribute("items", withdraws);
        return "adm/withdraw/list";
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/{withdrawApplyId}")
    public String withdrawProcess(@PathVariable long withdrawApplyId) {
        withdrawService.process(withdrawApplyId);
        return "redirect:/adm/withdraw/applyList";
    }

    @PostMapping("/rebate")
    @PreAuthorize("hasAuthority('ADMIN')")
    public String rebate(String ids, HttpServletRequest req) {

        String[] idsArr = ids.split(",");

        Arrays.stream(idsArr)
                .mapToLong(Long::parseLong)
                .forEach(id -> {
                    withdrawService.process(id);
                });

        return "redirect:/adm/withdraw/applyList";
    }
}
