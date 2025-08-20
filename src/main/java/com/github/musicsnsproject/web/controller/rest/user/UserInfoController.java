package com.github.musicsnsproject.web.controller.rest.user;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.github.musicsnsproject.domain.PostVO;
import com.github.musicsnsproject.service.mypage.MypageService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("api/userInfo")
@RequiredArgsConstructor
public class UserInfoController {

	private final MypageService service;
	
	
	@GetMapping("post")
	public List<PostVO> getUserPost(@AuthenticationPrincipal Long userId){
		Long fakeUserId = 41L;
		
		return service.getUserPost(fakeUserId);
	}
	
	
}
