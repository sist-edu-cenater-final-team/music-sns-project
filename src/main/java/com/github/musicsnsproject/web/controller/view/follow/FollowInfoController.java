package com.github.musicsnsproject.web.controller.view.follow;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/mypage/")
@RequiredArgsConstructor
public class FollowInfoController {

	
	@GetMapping("updateInfo")
	public String updateInfo() {
		
		return "mypage/updateInfo";
	}
	
	@GetMapping("myinfo")
	public String myInfoPage() {

		return "follow/mypage/myinfo";
	}

	@PostMapping("myinfo")
	public String myInfoPage(HttpServletRequest request,@RequestParam("targetUserId") Long targetUserId) {

		request.setAttribute("targetUserId", targetUserId);
		
		return "follow/mypage/myinfo";
	}
	
	@GetMapping("myFollowers")
	public String myFollower() {
		
		return "follow/follwers";
	}
	
}
