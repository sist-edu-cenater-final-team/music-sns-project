package com.github.musicsnsproject.web.controller.rest.account.auth;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test")
public class TestController {
    @GetMapping("/1")
    public Long test1(@AuthenticationPrincipal Long userId){
        System.out.println("test1");
        return userId;

    }
    @GetMapping("/2")
    public Long test2(@AuthenticationPrincipal Long userId){
        System.out.println("test1");
        return userId;
    }

    @GetMapping("/3")
    public Long test3(@AuthenticationPrincipal Long userId){
        System.out.println("test1");
        return userId;
    }
}
