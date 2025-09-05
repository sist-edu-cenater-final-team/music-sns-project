package com.github.musicsnsproject.repository.mybatis.dao.eumpyo;

import java.util.HashMap;
import java.util.Map;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class EumpyoChargeDAO_imple implements EumpyoChargeDAO {
	
    private final SqlSessionTemplate sql;

    // 현재 사용자 코인 조회 (잠금/동시수정불가)
    @Override
    public Long selectUserCoinForUpdate(long userId) {
        return sql.selectOne("eumpyoCharge.selectUserCoinForUpdate", userId);
    }

    
    // 최종 잔액 그대로 저장
    @Override
    public int setUserCoin(long userId, long coin) {
        Map<String, Object> map = new HashMap<>();
        map.put("userId", userId);
        map.put("coin", coin);
        return sql.update("eumpyoCharge.setUserCoin", map);
    }

    
    // 충전 이력 저장(충전 후 잔액 포함)
    @Override
    public int insertChargeHistory(long userId, int coin, int atThatPrice, long afterBalance) {
        Map<String, Object> map = new HashMap<>();
        map.put("userId", userId);
        map.put("coin", coin);
        map.put("atThatPrice", atThatPrice);
        map.put("afterBalance", afterBalance);
        return sql.insert("eumpyoCharge.insertChargeHistory", map);
    }

    
    // 현재 사용자 코인 조회 (단순 조회)
    @Override
    public Long selectUserCoin(long userId) {
        return sql.selectOne("eumpyoCharge.selectUserCoin", userId);
    }
}
