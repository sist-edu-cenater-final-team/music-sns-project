package com.github.musicsnsproject.service.mypage;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.github.musicsnsproject.domain.PostVO;
import com.github.musicsnsproject.domain.user.MyUserVO;
import com.github.musicsnsproject.repository.jpa.account.user.MyUserRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MypageService_imple implements MypageService {

	private final MyUserRepository repository;
	
	
	@Override
	public List<PostVO> getUserPost(Long userId) {
		
		return repository.getUserPost(userId);
	}


	@Override
	public MyUserVO getUserInfo(Long userId) {
		
		return repository.getUserInfo(userId);
	}


	@Override
	@Transactional
	public long updateUserInfo(Map<String, Object> paraMap) {
		return repository.updateUserInfo(paraMap);
	}

}
