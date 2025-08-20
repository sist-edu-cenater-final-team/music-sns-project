package com.github.musicsnsproject.repository.mybatis.dao.eumpyo;

public interface EumpyoPurchaseDAO {

	// 구매 시 사용자 코인 차감
    int decreaseUserCoin(long userId, int usedCoin);
    
    // 구매 내역(insert) 기록
    int insertPurchaseHistory(long userId, String musicId, int usedCoin);
    
    // 사용자 현재 코인 조회 (users.coin)
    Long selectUserCoin(long userId);
    
    // 충전/구매 내역 기반으로 코인 재계산
    int recalcUserCoinFromHistory(long userId);

}