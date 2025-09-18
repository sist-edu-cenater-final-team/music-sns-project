package com.github.musicsnsproject.web.controller.view.chat;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/chat")

public class ChatViewController {
    @GetMapping("/test")
    public String chatTestPage(){
        return "chat/chat";
    }
}
