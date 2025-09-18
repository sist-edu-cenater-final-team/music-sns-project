package com.github.musicsnsproject.service.follow;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.github.musicsnsproject.domain.ProfileMusicVO;
import com.github.musicsnsproject.domain.follow.FollowVO;
import com.github.musicsnsproject.domain.user.MyUserVO;
import com.github.musicsnsproject.repository.jpa.account.follow.FollowRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FollowService_imple implements FollowService {

	private final FollowRepository followRepository;
	
	@Override
	public List<FollowVO> getFollowerList(Long userId, Long viewUserId) {
		
		return followRepository.findByFollowerAndUserInfo(userId, viewUserId);
	}

	
	@Override
	public List<FollowVO> getFolloweeList(Long userId, Long viewUserId) {
		return followRepository.findByFolloweeAndUserInfo(userId, viewUserId);
	}


	@Override
	public List<FollowVO> findCommonFriend(Long userId) {
		return followRepository.findCommonFriend(userId);
	}


	@Override
	@Transactional
	public int addFollow(Map<String, Long> map) {

		return followRepository.addFollow(map);
	}


	@Override
	public List<FollowVO> searchUser(String searchWord, Long userId) {
		
		return followRepository.searchUser(searchWord, userId);
	}


	@Override
	@Transactional
	public long unFollow(Map<String, Long> map) {
		return followRepository.unFollow(map);
	}


	@Override
	public List<FollowVO> getfavoriteList(Long userId, Long viewUserId) {
		return followRepository.getfavoriteList(userId, viewUserId);
	}


	@Override
	@Transactional
	public long unFavorite(Map<String, Long> map) {		
		return followRepository.unFavorite(map);
	}


	@Override
	@Transactional
	public long addFavorite(Map<String, Long> map) {
		return followRepository.addFavorite(map);
	}


	@Override
	@Transactional
	public long addBlock(Map<String, Long> map) {
		
		return followRepository.addBlock(map);
	}



	@Override
	@Transactional
	public long unBlock(Map<String, Long> map) {
		return followRepository.unBlock(map);
	}


	@Override
	public List<MyUserVO> blockedList(Long userId) {
		return followRepository.blockedList(userId);
	}




	
}
