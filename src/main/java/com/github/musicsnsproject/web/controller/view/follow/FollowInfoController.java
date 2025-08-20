package com.github.musicsnsproject.web.controller.view.follow;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.github.musicsnsproject.service.mypage.MypageService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/mypage/")
@RequiredArgsConstructor
public class FollowInfoController {

	private final MypageService service;
	
	@GetMapping("myinfo")
	public String myInfoPage() {
		
		
		
		return "follow/mypage/myinfo";
	}
	
	@GetMapping("myFollowers")
	public String myFollower(HttpServletRequest request) {
		request.setAttribute("myId", "41");
		
		return "follow/follwers";
	}
	
}
