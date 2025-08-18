package com.github.musicsnsproject.repository.mybatis.dao.eumpyo;

import java.util.List;
import java.util.Map;

public interface EumpyoHistoryDAO {

    // 충전 내역 총 건수
    int countChargeHistory(long userId);

    // 충전 내역 페이지 조회
    List<Map<String, Object>> findChargeHistoryPage(long userId, int offset, int limit);

    // 사용 내역 총 건수
    int countUseHistory(long userId);

    // 사용 내역 페이지 조회
    List<Map<String, Object>> findUseHistoryPage(long userId, int offset, int limit);
}