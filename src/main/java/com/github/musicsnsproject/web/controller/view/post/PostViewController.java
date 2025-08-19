package com.github.musicsnsproject.web.controller.view.post;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/post/")
public class PostViewController {

    @GetMapping("/postView/{postId}")
    public String postViewByPath(@PathVariable Long postId) {

        

        return "post/postView";
    }


}
