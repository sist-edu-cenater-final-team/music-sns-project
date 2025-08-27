package com.github.musicsnsproject.web.controller.rest.user;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.github.musicsnsproject.common.myenum.Gender;
import com.github.musicsnsproject.domain.PostVO;
import com.github.musicsnsproject.domain.user.MyUserInfo;
import com.github.musicsnsproject.domain.user.MyUserVO;
import com.github.musicsnsproject.service.mypage.MypageService;
import com.querydsl.core.BooleanBuilder;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/userInfo")
@RequiredArgsConstructor
public class UserInfoController {

	private final MypageService service;

	@GetMapping("/getInfo")
	public MyUserInfo getInfo(
						        @AuthenticationPrincipal Long userId,
						        @RequestParam(value = "targetUserId", required = false) Long targetUserId) {

	    // targetUserId가 null이면 내 프로필 조회
	    Long targetId = (targetUserId != null) ? targetUserId : userId;

	    MyUserVO user = service.getUserInfo(targetId);
	    boolean isOwner = userId.equals(targetId);
	    
	    return new MyUserInfo(user, isOwner);
	}
	
	
	@GetMapping("/post")
	public List<PostVO> getUserPost(@AuthenticationPrincipal Long userId, @RequestParam(value = "targetUserId", required = false) Long targetUserId){
		if(targetUserId != null) {
			userId = targetUserId;
		}
		
		return service.getUserPost(userId);
	}
	
	@PostMapping("/update")
	public long udateInfo(@RequestParam(value="profile_image",required = false) String profile_image, 
						 @AuthenticationPrincipal Long userId,
						 @RequestParam String nickname,
						 @RequestParam String profileMessage,
						 @RequestParam Gender gender) {
		
		
		
		Map<String, Object> paraMap = new HashMap<>();
		paraMap.put("profile_image", profile_image);
		paraMap.put("userId", userId);
		paraMap.put("nickname", nickname);
		paraMap.put("profileMessage", profileMessage);
		paraMap.put("gender", gender);
		
		return service.updateUserInfo(paraMap);
	}
	
}
