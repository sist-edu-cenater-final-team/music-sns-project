package com.github.musicsnsproject.service.follow;

import java.util.List;

import com.github.musicsnsproject.domain.follow.FollowVO;

public interface FollowService {

	// 나를 팔로우 하는 사람
	List<FollowVO> getFollowerList(String userId);
	// 내가 팔로우 하는 사람
	List<FollowVO> getFolloweeList(String userId);
	// 함께 아는 친구
	List<FollowVO> findCommonFriend(String userId);

}
