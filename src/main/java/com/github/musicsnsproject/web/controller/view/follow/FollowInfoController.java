package com.github.musicsnsproject.web.controller.view.follow;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.github.musicsnsproject.repository.jpa.account.follow.Follow;

import jakarta.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/mypage/")
public class FollowInfoController {

	@GetMapping("myinfo")
	public String myInfoPage() {
		
		return "follow/myinfo";
	}
	
	@GetMapping("myFollowers")
	public String myFollower(HttpServletRequest request) {
		request.setAttribute("myId", "41");
		
		return "follow/follwers";
	}
	
}
