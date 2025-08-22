package com.github.musicsnsproject.repository.mybatis.dao.eumpyo;

import lombok.RequiredArgsConstructor;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class EumpyoPurchaseDAO_imple implements EumpyoPurchaseDAO {

    private final SqlSessionTemplate sql;

    // 구매 내역 1건 추가
    @Override
    public int insertPurchaseHistory(long userId, String musicId, int usedCoin) {
        Map<String, Object> map = new HashMap<>();
        map.put("userId", userId);
        map.put("musicId", musicId);
        map.put("usedCoin", usedCoin);
        return sql.insert("eumpyoPurchase.insertPurchaseHistory", map);
    }

    // 현재 사용자 코인 조회 
    @Override
    public Long selectUserCoin(long userId) {
        return sql.selectOne("eumpyoPurchase.selectUserCoin", userId);
    }

    // 사용자 음표 재정산 (충전/구매 내역 기준)
    @Override
    public int recalcUserCoinFromHistory(long userId) {
        Map<String, Object> map = new HashMap<>();
        map.put("userId", userId);
        return sql.update("eumpyoPurchase.recalcUserCoinFromHistory", map);
    }
}
