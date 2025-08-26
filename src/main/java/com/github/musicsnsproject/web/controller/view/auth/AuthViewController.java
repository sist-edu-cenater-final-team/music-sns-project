package com.github.musicsnsproject.web.controller.view.auth;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/auth")
@Controller
public class AuthViewController {
    @RequestMapping("/login")
    public String login(Model model) {
        model.addAttribute("boot", "5.3.2");
        return "auth/login";
    }

}
