package com.github.musicsnsproject.web.controller.view.cart;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("cart")
public class CartController {

    // 장바구니
    @GetMapping("list")
    public String musicCart(){
        return "cart/cartList";
    }
}
