package com.ll.finalproject.app.member.controller;

import com.ll.finalproject.app.member.entity.Member;
import com.ll.finalproject.app.member.exception.JoinEmailDuplicatedException;
import com.ll.finalproject.app.member.exception.JoinUsernameDuplicatedException;
import com.ll.finalproject.app.member.form.MemberJoinForm;
import com.ll.finalproject.app.member.form.MemberModifyForm;
import com.ll.finalproject.app.member.form.MemberModifyPasswordForm;
import com.ll.finalproject.app.member.service.MailService;
import com.ll.finalproject.app.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;
import java.security.Principal;

@Controller
@RequestMapping("/member")
@RequiredArgsConstructor
@Slf4j
public class MemberController {

    private final MemberService memberService;
    private final MailService mailService;

    @PreAuthorize("isAnonymous()")
    @GetMapping("/login")
    public String showLogin() {
        return "member/login";
    }

    @PreAuthorize("isAnonymous()")
    @PostMapping("/login")
    public String login() {
        return "redirect:/";
    }

    @PreAuthorize("isAnonymous()")
    @GetMapping("/join")
    public String showJoin(Model model) {
        model.addAttribute("memberJoinForm", new MemberJoinForm());
        return "member/join";
    }

    @PreAuthorize("isAnonymous()")
    @PostMapping("/join")
    public String join(@Valid MemberJoinForm memberJoinForm, BindingResult bindingResult) {

        log.info("memberJoinForm = {}", memberJoinForm);

        if (bindingResult.hasErrors()) {
            return "member/join";
        }

        if (!memberJoinForm.getPassword().equals(memberJoinForm.getPasswordConfirm())) {
            bindingResult.rejectValue("passwordConfirm",null,"패스워드와 패스워드 확인이 불일치합니다.");
            return "member/join";
        }

        try {
            memberService.join(memberJoinForm.getUsername(), memberJoinForm.getPassword(),
                    memberJoinForm.getEmail(), memberJoinForm.getNickname());
        } catch (JoinUsernameDuplicatedException e) {
            bindingResult.rejectValue("username", null, e.getMessage());
            return "member/join";
        } catch (JoinEmailDuplicatedException e) {
            bindingResult.rejectValue("email", null, e.getMessage());
            return "member/join";
        }

        mailService.sendMail(memberJoinForm.getEmail());

        return "redirect:/";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/profile")
    public String showProfile(Principal principal, Model model) {
        Member member = memberService.findByUsername(principal.getName()).orElse(null);
        if (member == null) {
            return "redirect:/";
        }

        model.addAttribute("memberModifyForm", member);
        return "member/profile";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/modify")
    public String showModify(Principal principal, Model model) {
        log.info("principal = {}", principal.getName());

        Member member = memberService.findByUsername(principal.getName()).orElse(null);
        if (member == null) {
            return "redirect:/";
        }
        model.addAttribute("memberModifyForm", member);
        return "member/modify";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/modify")
    public String modify(@Valid MemberModifyForm memberModifyForm, BindingResult bindingResult, Principal principal) {

        if (bindingResult.hasErrors()) {
            return "member/modify";
        }

        Member member = memberService.findByUsername(principal.getName()).orElse(null);
        if (member == null) {
            return "redirect:/";
        }

        memberService.modify(member, memberModifyForm.getEmail(), memberModifyForm.getNickname());
        return "redirect:/member/profile";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/modifyPassword")
    public String showModifyPassword(Model model) {
        model.addAttribute("memberModifyPasswordForm", new MemberModifyPasswordForm());
        return "member/modifyPassword";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/modifyPassword")
    public String modifyPassword(@Valid MemberModifyPasswordForm memberModifyPasswordForm, BindingResult bindingResult, Principal principal) {

        if (bindingResult.hasErrors()) {
            return "member/modifyPassword";
        }

        Member member = memberService.findByUsername(principal.getName()).orElse(null);
        if (member == null) {
            return "redirect:/";
        }

        log.info("memberModifyPasswordForm = {}", memberModifyPasswordForm);

        if (!memberModifyPasswordForm.getNewPassword().equals(memberModifyPasswordForm.getNewPasswordConfirm())) {
            bindingResult.rejectValue("newPassword",null,"패스워드와 패스워드 확인이 불일치합니다.");
            return "member/modifyPassword";
        }

        if (!memberService.checkOldPassword(memberModifyPasswordForm.getOldPassword(), member.getPassword())) {
            bindingResult.rejectValue("oldPassword",null,"기존 패스워드가 불일치합니다.");
            return "member/modifyPassword";
        }

        memberService.modifyPassword(member, memberModifyPasswordForm.getNewPassword());

        return "redirect:/member/profile";
    }
}
