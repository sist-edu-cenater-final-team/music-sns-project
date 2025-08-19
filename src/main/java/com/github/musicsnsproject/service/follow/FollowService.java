package com.github.musicsnsproject.service.follow;

import java.util.List;
import java.util.Map;

import com.github.musicsnsproject.domain.follow.FollowVO;

public interface FollowService {

	// 나를 팔로우 하는 사람
	List<FollowVO> getFollowerList(String userId);
	// 내가 팔로우 하는 사람
	List<FollowVO> getFolloweeList(String userId);
	// 즐겨찾기 유저
	List<FollowVO> getfavoriteList(String userId);
	// 함께 아는 친구
	List<FollowVO> findCommonFriend(String userId);
	
	// 언팔로우 하기
	long unFollow(Map<String, String> map);
	// 팔로우 하기
	int addFollow(Map<String, String> map);


	// 즐겨찾기 추가
	long addFavorite(Map<String, String> map);
	// 즐겨찾기 삭제
	long unFavorite(Map<String, String> map);

	
	// 검색
	List<FollowVO> searchUser(String searchWord, String userId);
	
	
	

}
