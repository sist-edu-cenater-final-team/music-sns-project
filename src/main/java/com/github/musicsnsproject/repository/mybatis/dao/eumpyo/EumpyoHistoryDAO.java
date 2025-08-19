package com.github.musicsnsproject.repository.mybatis.dao.eumpyo;

import java.util.List;
import java.util.Map;

public interface EumpyoHistoryDAO {

    // 충전내역 총 건수
    int countChargeHistory(long userId);

    // 충전내역 페이지 조회
    List<Map<String, Object>> findChargeHistoryPage(long userId, int offset, int limit);

    // 구매내역 총 건수
    int countPurchaseHistory(long userId);

    // 구매내역 페이지 조회
    List<Map<String, Object>> findPurchaseHistoryPage(long userId, int offset, int limit);
}