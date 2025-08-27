package com.github.musicsnsproject.web.controller.view.follow;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

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

	
	@GetMapping("myFollowers")
	public String myFollower() {
		
		return "follow/follwers";
	}
	
}
