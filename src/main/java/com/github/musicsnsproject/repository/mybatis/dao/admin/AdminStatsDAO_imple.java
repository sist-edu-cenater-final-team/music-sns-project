package com.github.musicsnsproject.repository.mybatis.dao.admin;

import java.util.List;
import java.util.Map;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class AdminStatsDAO_imple implements AdminStatsDAO {

    private final SqlSessionTemplate sql;

    @Override
    public Long sumChargedCoin(Map<String, String> param) {
        return sql.selectOne("adminStats.sumChargedCoin", param);
    }

    @Override
    public Long sumRevenue(Map<String, String> param) {
        return sql.selectOne("adminStats.sumRevenue", param);
    }

    @Override
    public Long sumUsedCoin(Map<String, String> param) {
        return sql.selectOne("adminStats.sumUsedCoin", param);
    }

    @Override
    public List<Map<String, Object>> seriesChargedCoin(Map<String, String> param) {
        return sql.selectList("adminStats.seriesChargedCoin", param);
    }

    @Override
    public List<Map<String, Object>> seriesUsedCoin(Map<String, String> param) {
        return sql.selectList("adminStats.seriesUsedCoin", param);
    }

    @Override
    public List<Map<String, Object>> topChargers(Map<String, String> param) {
        return sql.selectList("adminStats.topChargers", param);
    }

    @Override
    public List<Map<String, Object>> topSpenders(Map<String, String> param) {
        return sql.selectList("adminStats.topSpenders", param);
    }

    @Override
    public List<Map<String, Object>> seriesNewMembers(Map<String, String> param) {
        return sql.selectList("adminStats.seriesNewMembers", param);
    }

    @Override
    public List<Map<String, Object>> memberStatusDist() {
        return sql.selectList("adminStats.memberStatusDist");
    }

    @Override
    public List<Map<String, Object>> topFollowers(Map<String, String> param) {
        return sql.selectList("adminStats.topFollowers", param);
    }

    @Override
    public List<Map<String, Object>> topMusic(Map<String, String> param) {
        return sql.selectList("adminStats.topMusic", param);
    }
}
