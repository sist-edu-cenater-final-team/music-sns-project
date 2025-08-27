package com.github.musicsnsproject.service.mypage;

import java.util.List;
import java.util.Map;

import com.github.musicsnsproject.domain.PostVO;
import com.github.musicsnsproject.domain.user.MyUserVO;

public interface MypageService {

	// 유저들 게시물 가져오기
	List<PostVO> getUserPost(Long userId);
	// 유저 상세 인포
	MyUserVO getUserInfo(Long fakeUserId);
	// 유저 업데이트
	long updateUserInfo(Map<String, Object> paraMap);


}
