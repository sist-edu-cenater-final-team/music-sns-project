package com.github.musicsnsproject.repository.jpa.account.follow;

import java.util.List;
import java.util.Map;

import com.github.musicsnsproject.domain.follow.FollowVO;

public interface FollowQueryRepository {
	
	// 나를 팔로우 하는 사람
	List<FollowVO> findByFollowerAndUserInfo(String userId);
	
	// 내가 팔로우 하는 사람
	List<FollowVO> findByFolloweeAndUserInfo(String userId);
	
	// 즐겨찾기 유저
	List<FollowVO> getfavoriteList(String userId);
	
	// 함께 아는 친구
	List<FollowVO> findCommonFriend(String userId);
	// 팔로우 추가
	int addFollow(Map<String, String> map);
	// 팔로우 취소
	long unFollow(Map<String, String> map);
		
	// 즐겨찾기 추가
	long addFavorite(Map<String, String> map);
	// 즐겨찾기 취소
	long unFavorite(Map<String, String> map);

		
	// 검색된 유저
	List<FollowVO> searchUser(String searchWord, String userId);
	
	

	
}
