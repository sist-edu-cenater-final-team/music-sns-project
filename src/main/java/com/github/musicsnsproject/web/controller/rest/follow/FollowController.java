package com.github.musicsnsproject.web.controller.rest.follow;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.github.musicsnsproject.domain.follow.FollowVO;
import com.github.musicsnsproject.service.follow.FollowService;

import lombok.RequiredArgsConstructor;


@RestController
@RequestMapping("/api/follow")
@RequiredArgsConstructor
public class FollowController {

	private final FollowService followService; 
	
	// 나를 팔로우 하는 사람
    @GetMapping("/follower/{userId}")
    public List<FollowVO> follower(@PathVariable("userId") String userId) {
        return followService.getFollowerList(userId);
    }

    // 내가 팔로우 하는 사람
    @GetMapping("/followee/{userId}")
    public List<FollowVO> followee(@PathVariable("userId") String userId) {
        return followService.getFolloweeList(userId);
    }
    
    // 함께 아는 친구
    @GetMapping("/findCommonFriend/{userId}")
    public List<FollowVO> findMutualConnections(@PathVariable("userId") String userId) {
    	return followService.findCommonFriend(userId);
    }
    
    // 팔로우 하기
    @GetMapping("addFollow")
    public int addFollow(@RequestParam("followee") String followee, @RequestParam("follower") String follower) {

    	Map<String, String> map = new HashMap<>();
    	map.put("followee", followee);
    	map.put("follower", follower);
    	
    	return followService.addFollow(map);
    }
    
    @GetMapping("searchUser")
    public List<FollowVO> searchUser(@RequestParam("searchWord") String searchWord,
    								 @RequestParam("userId") String userId){
    	
    	
    	return followService.searchUser(searchWord, userId);
    }
    
    
	
	
}
