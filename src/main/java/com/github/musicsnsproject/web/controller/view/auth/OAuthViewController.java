package com.github.musicsnsproject.web.controller.view.auth;

import com.github.musicsnsproject.common.myenum.OAuthProvider;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/oauth")
public class OAuthViewController {
    @GetMapping("/{provider}/callback")
    public String oauthCallback(@PathVariable OAuthProvider provider, Model model) {
        model.addAttribute("provider", provider);

        return "auth/oauth/callback";
    }
}
