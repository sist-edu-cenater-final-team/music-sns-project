package com.github.musicsnsproject.service.mypage.eumpyo;

import java.util.Map;


public interface EumpyoChargeService {

	// 결제 준비
    Map<String, Object> requestCharge(long userId, int amount);

    // 결제 확정(검증)
    Map<String, Object> completeCharge(long userId, String impUid, String merchantUid);

    // 보유 음표
    Long getUserCoin(long userId);
}