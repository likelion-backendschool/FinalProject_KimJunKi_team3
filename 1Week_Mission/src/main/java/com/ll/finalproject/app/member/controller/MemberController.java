package com.ll.finalproject.app.member.controller;

import com.ll.finalproject.app.base.rq.Rq;
import com.ll.finalproject.app.member.entity.Member;
import com.ll.finalproject.app.member.exception.JoinEmailDuplicatedException;
import com.ll.finalproject.app.member.exception.JoinUsernameDuplicatedException;
import com.ll.finalproject.app.member.exception.MemberNotFoundException;
import com.ll.finalproject.app.member.form.*;
import com.ll.finalproject.app.member.service.MailService;
import com.ll.finalproject.app.member.service.MemberService;
import com.ll.finalproject.app.security.dto.MemberContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.utility.RandomString;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.security.Principal;
import java.util.Random;

@Controller
@RequestMapping("/member")
@RequiredArgsConstructor
@Slf4j
public class MemberController {

    private final MemberService memberService;
    private final Rq rq;

    @PreAuthorize("isAnonymous()")
    @GetMapping("/login")
    public String showLogin(HttpServletRequest request) {
        String uri = request.getHeader("Referer");
        if (uri != null && !uri.contains("/member/login")) {
            request.getSession().setAttribute("prevPage", uri);
        }

        return "member/login";
    }

    @PreAuthorize("isAnonymous()")
    @GetMapping("/join")
    public String showJoin(@ModelAttribute MemberJoinForm memberJoinForm) {
        return "member/join";
    }

    @PreAuthorize("isAnonymous()")
    @PostMapping("/join")
    public String join(@Valid MemberJoinForm memberJoinForm, BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return "member/join";
        }

        if (!memberJoinForm.getPassword().equals(memberJoinForm.getPasswordConfirm())) {
            bindingResult.rejectValue("passwordConfirm",null,"패스워드와 패스워드 확인이 불일치합니다.");
            return "member/join";
        }

        try {
            memberService.join(memberJoinForm.getUsername(), memberJoinForm.getPassword(), memberJoinForm.getEmail());
        } catch (JoinUsernameDuplicatedException e) {
            bindingResult.rejectValue("username", null, e.getMessage());
            return "member/join";
        } catch (JoinEmailDuplicatedException e) {
            bindingResult.rejectValue("email", null, e.getMessage());
            return "member/join";
        }

        return "redirect:/";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/profile")
    public String showProfile() {
        log.info("Asdsadas = {}", rq.getMember().getEmail());
        return "member/profile";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/modify")
    public String showModify(Model model) {
        Member member = rq.getMember();

        model.addAttribute("memberModifyForm", new MemberModifyForm(member.getEmail(), member.getNickname()));
        return "member/modify";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/modify")
    public String modify(@Valid MemberModifyForm memberModifyForm, BindingResult bindingResult, Principal principal) {

        if (bindingResult.hasErrors()) {
            return "member/modify";
        }

        memberService.modify(principal.getName(), memberModifyForm.getEmail(), memberModifyForm.getNickname());
        return "redirect:/member/profile";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/modifyPassword")
    public String showModifyPassword(@ModelAttribute MemberModifyPasswordForm memberModifyPasswordForm) {
        return "member/modifyPassword";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/modifyPassword")
    public String modifyPassword(@Valid MemberModifyPasswordForm memberModifyPasswordForm, BindingResult bindingResult, Principal principal) {

        if (bindingResult.hasErrors()) {
            return "member/modifyPassword";
        }

        if (!memberModifyPasswordForm.getNewPassword().equals(memberModifyPasswordForm.getNewPasswordConfirm())) {
            bindingResult.rejectValue("newPassword",null,"패스워드와 패스워드 확인이 불일치합니다.");
            return "member/modifyPassword";
        }

        memberService.modifyPassword(principal.getName(), memberModifyPasswordForm.getOldPassword(), memberModifyPasswordForm.getNewPassword());

        return "redirect:/member/profile";
    }

    @PreAuthorize("isAnonymous()")
    @GetMapping("/findUsername")
    public String showFindUsername(@ModelAttribute MemberModifyForm memberModifyForm) {
        return "member/findUsername";
    }

    @PreAuthorize("isAnonymous()")
    @PostMapping("/findUsername")
    public String findUsername(@Valid MemberModifyForm memberModifyForm, BindingResult bindingResult) {

        try {
            Member member = memberService.findByEmail(memberModifyForm.getEmail());
            bindingResult.reject(null, "아이디는 " + member.getUsername() + "입니다.");
        } catch (MemberNotFoundException e) {
            bindingResult.rejectValue("email",null, e.getMessage());
        }

        return "member/findUsername";
    }

    @PreAuthorize("isAnonymous()")
    @GetMapping("/findPassword")
    public String showFindPassword(Model model) {
        model.addAttribute("memberFindPasswordForm", new MemberFindPasswordForm());
        return "member/findPassword";
    }

    @PreAuthorize("isAnonymous()")
    @PostMapping("/findPassword")
    public String findPassword(@Valid MemberFindPasswordForm memberFindPasswordForm, BindingResult bindingResult) {

        Member member = memberService.findByUsername(memberFindPasswordForm.getUsername()).orElse(null);
        if (member == null) {
            bindingResult.rejectValue("username",null, "존재하지 않는 아이디입니다.");
            return "member/findPassword";
        }

        if (!memberFindPasswordForm.getEmail().equals(member.getEmail())) {
            bindingResult.rejectValue("email",null, "이메일이 틀렸습니다.");
            return "member/findPassword";
        }

        // 10자리수의 임시 비밀번호 생성
        String tempPassword = new RandomString(10, new Random()).nextString();
        log.info("rs = {}", tempPassword);
//        memberService.modifyPassword(member, tempPassword);

        bindingResult.reject(null, "이메일이 전송되었습니다.\n 1~2분의 시간이 소요될 수 있습니다.");

        return "member/findPassword";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/beAuthor")
    public String showBeAuthor(@ModelAttribute("memberBeAuthorForm") MemberBeAuthorForm memberBeAuthorForm) {
        return "member/beAuthor";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/beAuthor")
    public String beAuthor(@Validated MemberBeAuthorForm memberBeAuthorForm, BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return "member/beAuthor";
        }

        Member member = rq.getMember();

        memberService.beAuthor(member, memberBeAuthorForm.getNickname());

        return "redirect:/member/profile";
    }
}
