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

    // 사용자 코인 증가
    @Override
    public int increaseUserCoin(long userId, int coin) {
        Map<String, Object> map = new HashMap<>();
        map.put("userId", userId);
        map.put("coin", coin);
        return sql.update("eumpyoCharge.increaseUserCoin", map);
    }

    
    // 코인 충전내역 기록
    @Override
    public int insertChargeHistory(long userId, int coin, int atThatPrice) { 
        Map<String, Object> map = new HashMap<>();
        map.put("userId", userId); 
        map.put("coin", coin);
        map.put("atThatPrice", atThatPrice); 
        return sql.insert("eumpyoCharge.insertChargeHistory", map);
    }

    
    // 사용자 현재 코인 조회 (users.coin)
    @Override
    public Long selectUserCoin(long userId) {
        return sql.selectOne("eumpyoCharge.selectUserCoin", userId);
    }
    
}
