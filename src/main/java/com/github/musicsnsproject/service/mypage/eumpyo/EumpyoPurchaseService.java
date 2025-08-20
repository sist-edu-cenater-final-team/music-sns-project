package com.github.musicsnsproject.service.mypage.eumpyo;

import java.util.Map;

public interface EumpyoPurchaseService {

	// 음원 구매 처리
    Map<String, Object> purchase(long userId, String musicId, int usedCoin);

    // 사용자 코인 재정산
    int recalcUserCoin(long userId);
    
}