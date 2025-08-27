package com.github.musicsnsproject.service.admin;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.github.musicsnsproject.repository.mybatis.dao.admin.AdminStatsDAO;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminStatsService_imple implements AdminStatsService {

    private final AdminStatsDAO dao;

    @Override
    public Long sumChargedCoin(Map<String, String> param) {
        return dao.sumChargedCoin(param);
    }

    @Override
    public Long sumRevenue(Map<String, String> param) {
        return dao.sumRevenue(param);
    }

    @Override
    public Long sumUsedCoin(Map<String, String> param) {
        return dao.sumUsedCoin(param);
    }

    @Override
    public List<Map<String, Object>> seriesChargedCoin(Map<String, String> param) {
        return dao.seriesChargedCoin(param);
    }

    @Override
    public List<Map<String, Object>> seriesUsedCoin(Map<String, String> param) {
        return dao.seriesUsedCoin(param);
    }

    @Override
    public List<Map<String, Object>> topChargers(Map<String, String> param) {
        return dao.topChargers(param);
    }

    @Override
    public List<Map<String, Object>> topSpenders(Map<String, String> param) {
        return dao.topSpenders(param);
    }

    @Override
    public List<Map<String, Object>> seriesNewMembers(Map<String, String> param) {
        return dao.seriesNewMembers(param);
    }

    @Override
    public List<Map<String, Object>> memberStatusDist() {
        return dao.memberStatusDist();
    }

    @Override
    public List<Map<String, Object>> topFollowers(Map<String, String> param) {
        return dao.topFollowers(param);
    }

    @Override
    public List<Map<String, Object>> topMusic(Map<String, String> param) {
        return dao.topMusic(param);
    }
}
