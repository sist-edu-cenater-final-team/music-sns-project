package com.github.musicsnsproject.web.controller.view.follow;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/mypage/")
public class FollowInfoController {

	@GetMapping("myinfo")
	public String myInfoPage() {
		
		return "follow/myinfo";
	}
	
	@GetMapping("myFollowers")
	public String myFollower() {
		
		return "follow/follwers";
	}
	
}
