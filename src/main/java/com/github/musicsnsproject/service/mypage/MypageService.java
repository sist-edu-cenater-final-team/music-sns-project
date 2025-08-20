package com.github.musicsnsproject.service.mypage;

import java.util.List;

import com.github.musicsnsproject.domain.PostVO;

public interface MypageService {

	// 유저들 게시물 가져오기
	List<PostVO> getUserPost(Long userId);

}
