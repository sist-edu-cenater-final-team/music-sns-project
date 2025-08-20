package com.github.musicsnsproject.web.controller.view.cart;

import org.eclipse.tags.shaded.org.apache.xpath.operations.Mod;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("cart")
public class CartController {

    // 장바구니
    @GetMapping("/list")
    public String musicCart(){

        return "cart/cart";
    }

    // 주문페이지
    @GetMapping("/order")
    public String musicCartOrder(){
        return "cart/order";
    }
}
