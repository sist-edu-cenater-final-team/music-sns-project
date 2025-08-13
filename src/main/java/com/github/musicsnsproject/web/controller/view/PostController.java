package com.github.musicsnsproject.web.controller.view;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/post/")
public class PostController {

    @GetMapping("postView")
    public String postView(){

        return "posts/post";
    }

}
