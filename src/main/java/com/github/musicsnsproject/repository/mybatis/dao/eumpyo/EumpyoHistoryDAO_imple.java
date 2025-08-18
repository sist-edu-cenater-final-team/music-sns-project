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

    @Override
    public int countChargeHistory(long userId) {
        return sql.selectOne("eumpyoHistory.countChargeHistory", userId);
    }

    @Override
    public List<Map<String, Object>> findChargeHistoryPage(long userId, int offset, int limit) {
        Map<String, Object> p = new HashMap<>();
        p.put("userId", userId);
        p.put("offset", offset);
        p.put("limit",  limit);
        return sql.selectList("eumpyoHistory.findChargeHistoryPage", p);
    }

    @Override
    public int countUseHistory(long userId) {
        return sql.selectOne("eumpyoHistory.countUseHistory", userId);
    }

    @Override
    public List<Map<String, Object>> findUseHistoryPage(long userId, int offset, int limit) {
        Map<String, Object> p = new HashMap<>();
        p.put("userId", userId);
        p.put("offset", offset);
        p.put("limit",  limit);
        return sql.selectList("eumpyoHistory.findUseHistoryPage", p);
    }
}
