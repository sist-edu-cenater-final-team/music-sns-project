package com.github.musicsnsproject.repository.mybatis.dao.eumpyo;

public interface EumpyoPurchaseDAO {

	// 구매 내역 1건 추가
    int insertPurchaseHistory(long userId, String musicId, int usedCoin);

    // 현재 사용자 코인 조회 
    Long selectUserCoin(long userId);

    // 사용자 음표 재정산 (충전/구매 내역 기준)
    int recalcUserCoinFromHistory(long userId);
}