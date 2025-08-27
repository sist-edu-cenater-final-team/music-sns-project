package com.github.musicsnsproject.service.follow;

import java.util.List;
import java.util.Map;

import com.github.musicsnsproject.domain.follow.FollowVO;

public interface FollowService {

	// 나를 팔로우 하는 사람
	List<FollowVO> getFollowerList(Long userId);
	// 내가 팔로우 하는 사람
	List<FollowVO> getFolloweeList(Long userId);
	// 즐겨찾기 유저
	List<FollowVO> getfavoriteList(Long userId);
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
	// 차단
	long addBlock(Map<String, Long> map);
	
	// 카운트 가져오기
	Long followeeCount(Long userId);
	Long followerCount(Long userId);
	Long favoriteCount(Long userId);
	
	
	

}
