package com.github.musicsnsproject.repository.mybatis.dao.eumpyo;

public interface EumpyoChargeDAO {

    // 사용자 코인 증가
    int increaseUserCoin(long userId, int coin);

    // 코인 충전 이력 저장
    int insertChargeHistory(long userId, int coin, int atThatPrice);

    // 사용자 현재 코인 조회
    Long selectUserCoin(long userId);
}