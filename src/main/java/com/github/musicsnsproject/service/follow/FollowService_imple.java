package com.github.musicsnsproject.service.follow;

import java.util.List;

import org.springframework.stereotype.Service;

import com.github.musicsnsproject.domain.follow.FollowVO;
import com.github.musicsnsproject.repository.jpa.account.follow.FollowRepository;

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
	
	
}
