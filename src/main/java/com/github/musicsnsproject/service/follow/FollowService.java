package com.github.musicsnsproject.service.follow;

import java.util.List;
import java.util.Map;

import com.github.musicsnsproject.domain.ProfileMusicVO;
import com.github.musicsnsproject.domain.follow.FollowVO;
import com.github.musicsnsproject.domain.user.MyUserVO;

public interface FollowService {

	// 나를 팔로우 하는 사람
	List<FollowVO> getFollowerList(Long userId, Long viewUserId);
	// 내가 팔로우 하는 사람
	List<FollowVO> getFolloweeList(Long userId, Long viewUserId);
	// 즐겨찾기 유저
	List<FollowVO> getfavoriteList(Long userId, Long viewUserId);
	// 함께 아는 친구
	List<FollowVO> findCommonFriend(Long userId);
	
	// 언팔로우 하기
	long unFollow(Map<String, Long> map);
	// 팔로우 하기
	int addFollow(Map<String, Long> map);


	// 즐겨찾기 추가
	long addFavorite(Map<String, Long> map);
	// 즐겨찾기 삭제
	long unFavorite(Map<String, Long> map);
	
	// 검색
	List<FollowVO> searchUser(String searchWord, Long userId);

	// 차단한 유저 리스트
	List<MyUserVO> blockedList(Long userId);
	
	// 차단
	long addBlock(Map<String, Long> map);
	
	// 차단 풀기
	long unBlock(Map<String, Long> map);
	
	

	
	

}
