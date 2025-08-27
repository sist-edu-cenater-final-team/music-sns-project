package com.github.musicsnsproject.repository.mybatis.dao.eumpyo;

public interface EumpyoChargeDAO {
	
	// 현재 사용자 코인 조회 (잠금/동시수정불가)
    Long selectUserCoinForUpdate(long userId);

    // 최종 잔액 그대로 저장
    int setUserCoin(long userId, long coin);

    // 충전 이력 저장(충전 후 잔액 포함)
    int insertChargeHistory(long userId, int coin, int atThatPrice, long afterBalance);

    // 현재 사용자 코인 조회 (단순 조회)
    Long selectUserCoin(long userId);
}