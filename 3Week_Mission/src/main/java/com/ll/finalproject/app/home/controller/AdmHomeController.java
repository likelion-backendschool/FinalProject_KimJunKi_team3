package com.ll.finalproject.app.home.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/adm")
public class AdmHomeController {

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("")
    public String showIndex() {
        return "redirect:/adm/home/main";
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/home/main")
    public String showMain() {
        return "adm/home/main";
    }
}