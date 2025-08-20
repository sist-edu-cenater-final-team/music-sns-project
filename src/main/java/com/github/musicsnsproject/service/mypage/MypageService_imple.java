package com.github.musicsnsproject.service.mypage;

import java.util.List;

import org.springframework.stereotype.Service;

import com.github.musicsnsproject.domain.PostVO;
import com.github.musicsnsproject.repository.jpa.account.user.MyUserQueryRepository;
import com.github.musicsnsproject.repository.jpa.account.user.MyUserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MypageService_imple implements MypageService {

	private final MyUserRepository repository;
	
	
	@Override
	public List<PostVO> getUserPost(Long userId) {
		
		return repository.getUserPost(userId);
	}

}
