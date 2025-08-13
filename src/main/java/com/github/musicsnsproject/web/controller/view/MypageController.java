package com.github.musicsnsproject.web.controller.view;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/mypage")
public class MypageController {
<<<<<<< HEAD
=======

    // 장바구니
>>>>>>> branch 'main' of https://github.com/sist-edu-cenater-final-team/music-sns-project.git
    @GetMapping("cart")
    public String musicCart(){
        return "mypage/cart";
    }
}
