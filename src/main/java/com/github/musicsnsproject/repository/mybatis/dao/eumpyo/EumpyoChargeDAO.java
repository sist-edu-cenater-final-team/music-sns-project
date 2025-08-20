package com.github.musicsnsproject.repository.mybatis.dao.eumpyo;

public interface EumpyoChargeDAO {

	// 사용자 코인 증가
    int increaseUserCoin(long userId, int coin);

    // 코인 충전내역 기록
    int insertChargeHistory(long userId, int coin, int atThatPrice);

    // 사용자 현재 코인 조회 (users.coin)
    Long selectUserCoin(long userId);
    
}