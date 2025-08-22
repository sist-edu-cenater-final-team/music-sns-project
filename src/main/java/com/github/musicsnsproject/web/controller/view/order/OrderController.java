package com.github.musicsnsproject.web.controller.view.order;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("order")
public class OrderController {

    // 주문 미리보기
    @GetMapping("preview")
    public String orderPreview(){
        return "order/orderPreview";
    }

    // 주문 완료페이지
    @GetMapping("complete")
    public String musicCartOrderComplete() {
        return "order/orderComplete";
    }
}
