package com.github.musicsnsproject.web.controller.view;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/mypage")
@RequiredArgsConstructor
public class MyPageController {

    @GetMapping("/eumpyo/charge")
    public String charge() {
        return "mypage/eumpyo/charge";
    }

    @GetMapping("/eumpyo/chargeHistory")
    public String chargeHistoryPage() {
        return "mypage/eumpyo/chargeHistory";
    }

    @GetMapping("/eumpyo/purchaseHistory")
    public String purchaseHistoryPage() {
        return "mypage/eumpyo/purchaseHistory";
    }
}
