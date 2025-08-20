package com.github.musicsnsproject.repository.mybatis.dao.eumpyo;

import lombok.RequiredArgsConstructor;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class EumpyoHistoryDAO_imple implements EumpyoHistoryDAO {

    private final SqlSessionTemplate sql;

    // 충전내역 총 건수
    @Override
    public int countChargeHistory(long userId) {
        return sql.selectOne("eumpyoHistory.countChargeHistory", userId);
    }

    
    // 충전내역 페이지 조회
    @Override
    public List<Map<String, Object>> findChargeHistoryPage(long userId, int offset, int size) {
        Map<String, Object> p = new HashMap<>();
        p.put("userId", userId);
        p.put("offset", Math.max(0, offset));
        p.put("size",  Math.max(1, size));
        return sql.selectList("eumpyoHistory.findChargeHistoryPage", p);
    }

    
    // 구매내역 총 건수
    @Override
    public int countPurchaseHistory(long userId) {
        return sql.selectOne("eumpyoHistory.countPurchaseHistory", userId);
    }
    
    
    // 구매내역 페이지 조회
    @Override
    public List<Map<String, Object>> findPurchaseHistoryPage(long userId, int offset, int size) {
        Map<String, Object> p = new HashMap<>();
        p.put("userId", userId);
        p.put("offset", Math.max(0, offset));
        p.put("size",  Math.max(1, size)); 
        return sql.selectList("eumpyoHistory.findPurchaseHistoryPage", p);
    }
}
