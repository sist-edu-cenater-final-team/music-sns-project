package com.github.musicsnsproject.repository.jpa.account.follow;

import java.util.List;

import com.github.musicsnsproject.domain.follow.FollowVO;

public interface FollowQueryRepository {
	
	// 나를 팔로우 하는 사람
	List<FollowVO> findByFollowerAndUserInfo(String userId);
	
	// 내가 팔로우 하는 사람
	List<FollowVO> findByFolloweeAndUserInfo(String userId);
	
	// 함께 아는 친구
	List<FollowVO> findCommonFriend(String userId);
	
}
