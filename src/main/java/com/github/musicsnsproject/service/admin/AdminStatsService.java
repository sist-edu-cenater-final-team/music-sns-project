package com.github.musicsnsproject.service.admin;

import java.util.List;
import java.util.Map;

public interface AdminStatsService {

    Long sumChargedCoin(Map<String, String> param);
    Long sumRevenue(Map<String, String> param);
    Long sumUsedCoin(Map<String, String> param);

    List<Map<String, Object>> seriesChargedCoin(Map<String, String> param);
    List<Map<String, Object>> seriesUsedCoin(Map<String, String> param);

    List<Map<String, Object>> topChargers(Map<String, String> param);
    List<Map<String, Object>> topSpenders(Map<String, String> param);

    List<Map<String, Object>> seriesNewMembers(Map<String, String> param);
    List<Map<String, Object>> memberStatusDist();

    List<Map<String, Object>> topFollowers(Map<String, String> param);
    List<Map<String, Object>> topMusic(Map<String, String> param);
}
