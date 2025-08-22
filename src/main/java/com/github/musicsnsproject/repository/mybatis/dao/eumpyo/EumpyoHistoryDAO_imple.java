package com.github.musicsnsproject.repository.mybatis.dao.eumpyo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;

// MyBatis SqlSessionTemplate으로 XML 쿼리 호출
@Repository
@RequiredArgsConstructor
public class EumpyoHistoryDAO_imple implements EumpyoHistoryDAO {

    private final SqlSessionTemplate sql; 

    // 충전내역 총 건수
    @Override
    public int countChargeHistory(long userId) {
        return sql.selectOne("eumpyoHistory.countChargeHistory", userId);
    }

    
    // 구매내역 총 건수
    @Override
    public int countPurchaseHistory(long userId) {
        return sql.selectOne("eumpyoHistory.countPurchaseHistory", userId);
    }

    
    // 충전내역 페이지 조회
    @Override
    public List<Map<String, Object>> findChargeHistoryPage(long userId, int offset, int size) {
    	
        Map<String, Object> map = new HashMap<>();
        
        map.put("userId", userId);   // 사용자 ID
        map.put("offset", offset);   // 페이징 시작 위치
        map.put("size", size);       // 페이지 크기
        
        return sql.selectList("eumpyoHistory.findChargeHistoryPage", map);
    }

    
    // 구매내역 페이지 조회
    @Override
    public List<Map<String, Object>> findPurchaseHistoryPage(long userId, int offset, int size) {
    	
        Map<String, Object> map = new HashMap<>();
        
        map.put("userId", userId);
        map.put("offset", offset);
        map.put("size", size);
        
        return sql.selectList("eumpyoHistory.findPurchaseHistoryPage", map);
    }
    
    
    // 로그인한 유저의 구매내역인지 확인
    @Override
    public boolean existsPurchaseByUser(long userId, long purchaseHistoryId) {
    	
        Map<String, Object> map = new HashMap<>();
        
        map.put("userId", userId);
        map.put("purchaseHistoryId", purchaseHistoryId);
        
        Integer cnt = sql.selectOne("eumpyoHistory.existsPurchaseByUser", map);
        
        return cnt != null && cnt > 0;
    }

    
    // 구매음악 조회 (purchase_music)
    @Override
    public List<Map<String, Object>> findPurchaseMusic(long userId, long purchaseHistoryId) {
    	
        Map<String, Object> map = new HashMap<>();
        
        map.put("userId", userId);
        map.put("purchaseHistoryId", purchaseHistoryId);
        
        return sql.selectList("eumpyoHistory.findPurchaseMusic", map);
    }
}
