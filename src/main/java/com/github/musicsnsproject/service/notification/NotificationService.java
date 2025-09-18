package com.github.musicsnsproject.service.notification;

import java.util.Map;

public interface NotificationService {

	// 알림 페이지 조회
	Map<String, Object> getNotificationPage(long recipientUserId, int pageNumber, int pageSize);

	// 모두 읽음 처리
	void markAllAsRead(long recipientUserId);

	// 개별 알림 삭제
	void deleteOne(long recipientUserId, String eventKey);

	// 전체 알림 삭제
	void deleteAll(long recipientUserId);

	// 개별 읽음 처리
	void markOneAsRead(long recipientUserId, String eventKey);
}
