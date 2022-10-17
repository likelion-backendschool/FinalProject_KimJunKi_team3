package com.ll.finalproject.app.member.controller;

import com.ll.finalproject.app.member.form.MemberJoinForm;
import com.ll.finalproject.app.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;

@Controller
@RequestMapping("/member")
@RequiredArgsConstructor
@Slf4j
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/login")
    public String showLogin() {
        return "member/login";
    }

    @PostMapping("/login")
    public String login() {
        return "redirect:/";
    }

    @GetMapping("/join")
    public String showJoin(Model model) {
        model.addAttribute("memberJoinForm", new MemberJoinForm());
        return "member/join";
    }

    @PostMapping("/join")
    public String join(@Valid @ModelAttribute("memberJoinForm") MemberJoinForm memberJoinForm, BindingResult bindingResult) {

        log.info("memberJoinForm = {}", memberJoinForm);

        if (bindingResult.hasErrors()) {
            return "member/join";
        }

        if (!memberJoinForm.getPassword().equals(memberJoinForm.getPasswordConfirm())) {
            bindingResult.rejectValue("passwordConfirm","패스워드와 패스워드 확인이 불일치합니다.");
        }

        memberService.join(memberJoinForm.getUsername(), memberJoinForm.getPassword(),
                memberJoinForm.getEmail(), memberJoinForm.getNickname());

        return "redirect:/";
    }
}
