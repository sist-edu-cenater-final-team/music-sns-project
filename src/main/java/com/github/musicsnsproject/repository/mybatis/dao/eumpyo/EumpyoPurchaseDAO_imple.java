package com.github.musicsnsproject.repository.mybatis.dao.eumpyo;

import java.util.HashMap;
import java.util.Map;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class EumpyoPurchaseDAO_imple implements EumpyoPurchaseDAO {

    private final SqlSessionTemplate sql;

    // 구매 시 사용자 코인 차감
    @Override
    public int decreaseUserCoin(long userId, int usedCoin) {
        Map<String, Object> p = new HashMap<>();
        p.put("userId", userId);
        p.put("usedCoin", usedCoin);
        return sql.update("eumpyoPurchase.decreaseUserCoin", p);
    }

    
    // 구매 내역(insert) 기록
    @Override
    public int insertPurchaseHistory(long userId, String musicId, int usedCoin) {
        Map<String, Object> p = new HashMap<>();
        p.put("userId", userId);
        p.put("musicId", musicId);
        p.put("usedCoin", usedCoin);
        return sql.insert("eumpyoPurchase.insertPurchaseHistory", p);
    }

    
    // 사용자 현재 코인 조회 (users.coin)
    @Override
    public Long selectUserCoin(long userId) {
        Map<String, Object> p = new HashMap<>();
        p.put("userId", userId);
        return sql.selectOne("eumpyoPurchase.selectUserCoin", p);
    }

    
    // 충전/구매 내역 기반으로 코인 재계산
    @Override
    public int recalcUserCoinFromHistory(long userId) {
        Map<String, Object> p = new HashMap<>();
        p.put("userId", userId);
        return sql.update("eumpyoPurchase.recalcUserCoinFromHistory", p);
    }

    
}
