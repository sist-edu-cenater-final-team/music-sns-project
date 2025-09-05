package com.github.musicsnsproject.repository.mybatis.dao.notification;

import java.util.List;
import java.util.Map;

public interface NotificationDAO {
	
	// 나를 팔로우한 사용자 이벤트 목록 조회
	List<Map<String, Object>> listFollows(Map<String, Object> params);

	// 내 게시글에 달린 좋아요 이벤트 목록 조회
	List<Map<String, Object>> listLikes(Map<String, Object> params);

	// 내 게시글에 달린 댓글 이벤트 목록 조회
	List<Map<String, Object>> listComments(Map<String, Object> params);
}
