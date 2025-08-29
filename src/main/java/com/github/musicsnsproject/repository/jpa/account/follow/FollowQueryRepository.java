package com.github.musicsnsproject.repository.jpa.account.follow;

import java.util.List;
import java.util.Map;

import com.github.musicsnsproject.domain.ProfileMusicVO;
import com.github.musicsnsproject.domain.follow.FollowVO;
import com.github.musicsnsproject.domain.user.MyUserVO;

public interface FollowQueryRepository {
	
	// 나를 팔로우 하는 사람
	List<FollowVO> findByFollowerAndUserInfo(Long userId);
	
	// 내가 팔로우 하는 사람
	List<FollowVO> findByFolloweeAndUserInfo(Long userId);
	
	// 즐겨찾기 유저
	List<FollowVO> getfavoriteList(Long userId);
	
	// 함께 아는 친구
	List<FollowVO> findCommonFriend(Long userId);
	// 팔로우 추가
	int addFollow(Map<String, Long> map);
	// 팔로우 취소
	long unFollow(Map<String, Long> map);
		
	// 즐겨찾기 추가
	long addFavorite(Map<String, Long> map);
	// 즐겨찾기 취소
	long unFavorite(Map<String, Long> map);
	// 차단
	long addBlock(Map<String, Long> map);	
	// 검색된 유저
	List<FollowVO> searchUser(String searchWord, Long userId);
	
	// 차단 풀기
	long unBlock(Map<String, Long> map);
	// 차단한 유저 리스트
	List<MyUserVO> blockedList(Long userId);

	
}
