package com.github.musicsnsproject.repository.mybatis.dao.eumpyo;

import java.util.Map;

public interface EumpyoPurchaseDAO {

	// 구매 내역 1건 추가
	int insertPurchaseHistory(Map<String, Object> params);

	// 해당 구매 내역에 속한 곡 N개 추가
	int insertPurchaseMusic(Map<String, Object> params);

	// 현재 사용자 코인 조회 (단순 조회)
	Long selectUserCoin(long userId);

	// 사용자 음표 재정산 (충전/구매 내역 기준)
	int recalcUserCoinFromHistory(long userId);

	// 현재 사용자 코인 조회 (잠금/동시수정불가)
	Long selectUserCoinForUpdate(long userId);

	// 구매내역 행의 at_that_user_coin 확정 반영
	int updatePurchaseHistoryBalance(Map<String, Object> params);

	// 구매 건만큼 코인 차감
	int decreaseUserCoin(Map<String, Object> params);
}
