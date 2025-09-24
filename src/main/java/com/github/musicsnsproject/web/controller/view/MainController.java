package com.github.musicsnsproject.web.controller.view;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
public class MainController {
    @GetMapping("")
    public String index(){
        return "redirect:music/chart";
    }
    @GetMapping("index")
    public String main(){
        return "index";
    }
}
