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

    // 기간 내 충전 코인 총합 조회
    @Override
    public Long sumChargedCoin(Map<String, String> param) {
        return sql.selectOne("adminStats.sumChargedCoin", param);
    }

    // 기간 내 매출 총합 조회
    @Override
    public Long sumRevenue(Map<String, String> param) {
        return sql.selectOne("adminStats.sumRevenue", param);
    }

    // 기간 내 사용 코인 총합 조회
    @Override
    public Long sumUsedCoin(Map<String, String> param) {
        return sql.selectOne("adminStats.sumUsedCoin", param);
    }

    // 일자별 충전 코인 집계
    @Override
    public List<Map<String, Object>> seriesChargedCoin(Map<String, String> param) {
        return sql.selectList("adminStats.seriesChargedCoin", param);
    }

    // 일자별 사용 코인 집계
    @Override
    public List<Map<String, Object>> seriesUsedCoin(Map<String, String> param) {
        return sql.selectList("adminStats.seriesUsedCoin", param);
    }

    // 코인 충전액 상위 10명 조회
    @Override
    public List<Map<String, Object>> topChargers(Map<String, String> param) {
        return sql.selectList("adminStats.topChargers", param);
    }

    // 코인 사용액 상위 10명 조회
    @Override
    public List<Map<String, Object>> topSpenders(Map<String, String> param) {
        return sql.selectList("adminStats.topSpenders", param);
    }

    // 일자별 신규 가입자 집계
    @Override
    public List<Map<String, Object>> seriesNewMembers(Map<String, String> param) {
        return sql.selectList("adminStats.seriesNewMembers", param);
    }

    // 회원 상태 분포 조회
    @Override
    public List<Map<String, Object>> memberStatusDist() {
        return sql.selectList("adminStats.memberStatusDist");
    }

    // 팔로워 수 상위 10명 조회
    @Override
    public List<Map<String, Object>> topFollowers(Map<String, String> param) {
        return sql.selectList("adminStats.topFollowers", param);
    }

    // 판매량/매출 상위 10개 음악 조회
    @Override
    public List<Map<String, Object>> topMusic(Map<String, String> param) {
        return sql.selectList("adminStats.topMusic", param);
    }
}
