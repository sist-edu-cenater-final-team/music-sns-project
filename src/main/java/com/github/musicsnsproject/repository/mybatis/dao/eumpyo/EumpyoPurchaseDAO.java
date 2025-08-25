package com.github.musicsnsproject.repository.mybatis.dao.eumpyo;

import java.util.Map;

public interface EumpyoPurchaseDAO {

	// 구매 내역 1건 추가
	int insertPurchaseHistory(Map<String, Object> params);

	// 해당 구매 내역에 속한 곡 N개 추가
    int insertPurchaseMusic(Map<String, Object> params);
    
    // 현재 사용자 코인 조회 
    Long selectUserCoin(long userId);

    // 사용자 음표 재정산 (충전/구매 내역 기준)
    int recalcUserCoinFromHistory(long userId);
}