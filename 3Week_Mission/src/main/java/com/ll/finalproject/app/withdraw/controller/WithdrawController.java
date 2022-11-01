package com.ll.finalproject.app.withdraw.controller;

import com.ll.finalproject.app.withdraw.dto.WithdrawForm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/withdraw")
public class WithdrawController {

    @GetMapping("/apply")
    public String showMakeData(@ModelAttribute WithdrawForm withdrawForm) {
        return "withdraw/apply";
    }
}
