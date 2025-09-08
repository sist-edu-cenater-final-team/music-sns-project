package com.github.musicsnsproject.web.controller.rest.follow;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.github.musicsnsproject.domain.ProfileMusicVO;
import com.github.musicsnsproject.domain.follow.FollowVO;
import com.github.musicsnsproject.domain.user.MyUserVO;
import com.github.musicsnsproject.service.follow.FollowService;

import lombok.RequiredArgsConstructor;


@RestController
@RequestMapping("/api/follow")
@RequiredArgsConstructor
public class FollowController {


	private final FollowService followService;

 
	
	// 나를 팔로우 하는 사람
    @GetMapping("/follower")
    public List<FollowVO> follower(@AuthenticationPrincipal Long userId,
    								@RequestParam(value = "targetUserId", required = false) Long targetUserId) {
    	Long viewUserId = null;
    	if(targetUserId != null) {
    		viewUserId = userId;
    		userId = targetUserId;
    		
    	}
    	
        return followService.getFollowerList(userId, viewUserId);
    }

    // 내가 팔로우 하는 사람
    @GetMapping("/followee")
    public List<FollowVO> followee(@AuthenticationPrincipal Long userId,
    								@RequestParam(value = "targetUserId", required = false) Long targetUserId) {
    	Long viewUserId = null;
    	if(targetUserId != null) {
    		viewUserId = userId;
    		userId = targetUserId;
    		
    	}
    	
        return followService.getFolloweeList(userId, viewUserId);
    }
    
    @GetMapping("/favorite")
    public List<FollowVO> favorite(@AuthenticationPrincipal Long userId,
    								@RequestParam(value = "targetUserId", required = false) Long targetUserId) {
    	Long viewUserId = null;
    	if(targetUserId != null) {
    		viewUserId = userId;
    		userId = targetUserId;
    		
    	}
    	
    	return followService.getfavoriteList(userId, viewUserId);
    }
    
    
    // 함께 아는 친구
    @GetMapping("/findCommonFriend")
    public List<FollowVO> findMutualConnections(@AuthenticationPrincipal Long userId) {
    	return followService.findCommonFriend(userId);
    }
    
    // 팔로우 하기
    @GetMapping("addFollow")
    public long addFollow(@RequestParam("followee") Long followee, @AuthenticationPrincipal Long userId) {

    	Map<String, Long> map = new HashMap<>();
    	map.put("followee", followee);
    	map.put("follower", userId);
    	
    	return followService.addFollow(map);
    }
    
    // 유저 검색
    @GetMapping("searchUser")
    public List<FollowVO> searchUser(@RequestParam("searchWord") String searchWord,
    								 @AuthenticationPrincipal Long userId){
    	
    	
    	return followService.searchUser(searchWord, userId);
    }
    
    // 언팔로우
    @GetMapping("unFollow")
    public long unFollow(@RequestParam("followee") Long followee, @AuthenticationPrincipal Long userId) {
    	
    	Map<String, Long> map = new HashMap<>();
    	map.put("followee", followee);
    	map.put("follower", userId);
    	
    	return followService.unFollow(map);
    }
	
    // 즐겨찾기 추가
    @GetMapping("addFavorite")
    public long addFavorite(@RequestParam("followee") Long followee, @AuthenticationPrincipal Long userId) {
    	
    	Map<String, Long> map = new HashMap<>();
    	map.put("followee", followee);
    	map.put("follower", userId);
    	
    	return followService.addFavorite(map);
    }
    
    
    // 즐겨찾기 삭제
    @GetMapping("unFavorite")
    public long unFavorite(@RequestParam("followee") Long followee, @AuthenticationPrincipal Long userId) {
    	
    	Map<String, Long> map = new HashMap<>();
    	map.put("followee", followee);
    	map.put("follower", userId);
    	
    	return followService.unFavorite(map);
    }
    
    @GetMapping("block")
    public long block(@AuthenticationPrincipal Long userId, @RequestParam("blockUser") Long blockUser) {
    	Map<String, Long> map = new HashMap<>();
    	map.put("userId", userId);
    	map.put("blockUser", blockUser);
    	return followService.addBlock(map);
    }
    
    @GetMapping("unBlock")
    public long unBlock(@AuthenticationPrincipal Long userId, @RequestParam("blockUser") Long blockUser) {
    	Map<String, Long> map = new HashMap<>();
    	map.put("userId", userId);
    	map.put("blockUser", blockUser);
    	return followService.unBlock(map);
    }
    
    @GetMapping("blockedList")
    public List<MyUserVO> blockedList(@AuthenticationPrincipal Long userId) {
    	System.out.println("유저아이디" + userId);
    	return followService.blockedList(userId);
    }
    
    
    
	
}
