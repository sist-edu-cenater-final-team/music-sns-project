package com.github.musicsnsproject.service.follow;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.github.musicsnsproject.domain.follow.FollowVO;
import com.github.musicsnsproject.repository.jpa.account.follow.FollowRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FollowService_imple implements FollowService {

	private final FollowRepository followRepository;
	
	
	@Override
	public List<FollowVO> getFollowerList(String userId) {
		
		return followRepository.findByFollowerAndUserInfo(userId);
	}

	
	@Override
	public List<FollowVO> getFolloweeList(String userId) {
		return followRepository.findByFolloweeAndUserInfo(userId);
	}


	@Override
	public List<FollowVO> findCommonFriend(String userId) {
		return followRepository.findCommonFriend(userId);
	}


	@Override
	@Transactional
	public int addFollow(Map<String, String> map) {

		return followRepository.addFollow(map);
	}


	@Override
	public List<FollowVO> searchUser(String searchWord, String userId) {
		
		return followRepository.searchUser(searchWord, userId);
	}


	@Override
	@Transactional
	public long unFollow(Map<String, String> map) {
		return followRepository.unFollow(map);
	}


	@Override
	public List<FollowVO> getfavoriteList(String userId) {
		return followRepository.getfavoriteList(userId);
	}


	@Override
	@Transactional
	public long unFavorite(Map<String, String> map) {		
		return followRepository.unFavorite(map);
	}


	@Override
	@Transactional
	public long addFavorite(Map<String, String> map) {
		return followRepository.addFavorite(map);
	}
	
	
}
