package com.github.musicsnsproject.web.controller.rest.user;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.github.musicsnsproject.domain.PostVO;
import com.github.musicsnsproject.domain.user.MyUserVO;
import com.github.musicsnsproject.service.mypage.MypageService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("api/userInfo")
@RequiredArgsConstructor
public class UserInfoController {

	private final MypageService service;

	@GetMapping("getInfo/{userId}")
	public MyUserVO getInfo(@PathVariable("userId") long userId) {
		return service.getUserInfo(userId);
	}
	
	
	@GetMapping("post")
	public List<PostVO> getUserPost(@AuthenticationPrincipal Long userId){
		Long fakeUserId = 41L;
		
		return service.getUserPost(fakeUserId);
	}
	
	@PostMapping("update")
	public int udateInfo(@RequestParam("profile_image") MultipartFile file, 
						 @RequestParam String userid,
						 @RequestParam String nickname,
						 @RequestParam String profileMessage) {
		Map<String, String> paraMap = new HashMap<>();
		paraMap.put("userid", userid);
		paraMap.put("nickname", nickname);
		paraMap.put("profileMessage", profileMessage);
		System.out.println(file);
		return 1;
	}
	
}
