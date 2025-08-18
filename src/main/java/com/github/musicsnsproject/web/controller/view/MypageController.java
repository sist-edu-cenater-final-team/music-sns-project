package com.github.musicsnsproject.web.controller.view;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/mypage")
public class MypageController {

    // 장바구니
    @GetMapping("cart")
    public String musicCart(){
        return "mypage/cart";
    }

    // 결제페이지
    @GetMapping("payment")
    public String payment(){
        return "mypage/payment";
    }
}
