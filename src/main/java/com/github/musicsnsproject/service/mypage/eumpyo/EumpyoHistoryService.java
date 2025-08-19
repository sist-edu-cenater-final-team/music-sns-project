package com.github.musicsnsproject.service.mypage.eumpyo;

import java.util.Map;

public interface EumpyoHistoryService {
	
	// 충전내역 조회
	Map<String, Object> getChargeHistory(long userId, int page, int size);
    
    // 구매내역 조회
	Map<String, Object> getPurchaseHistory(long userId, int page, int size);
    
}