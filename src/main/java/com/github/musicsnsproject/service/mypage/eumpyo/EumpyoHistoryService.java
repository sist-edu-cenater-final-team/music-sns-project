package com.github.musicsnsproject.service.mypage.eumpyo;

import java.util.Map;

public interface EumpyoHistoryService {
	
	// 충전 내역 조회
	Map<String, Object> getChargeHistory(long userId, int page, int size);
    
    // 사용 내역 조회
	Map<String, Object> getUseHistory(long userId, int page, int size);
    
}