package com.github.musicsnsproject.service.mypage.eumpyo;

import java.util.Map;

// 컨트롤러에서 사용할 서비스 인터페이스
public interface EumpyoHistoryService {

    // 충전내역 페이지 조회
	Map<String, Object> getChargeHistory(long userId, int page, int size);

    // 구매내역 페이지 조회
	Map<String, Object> getPurchaseHistory(long userId, int page, int size);
}
