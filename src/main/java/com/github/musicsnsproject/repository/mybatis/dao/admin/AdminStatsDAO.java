package com.github.musicsnsproject.repository.mybatis.dao.admin;

import java.util.List;
import java.util.Map;

public interface AdminStatsDAO {

	// 기간 내 충전 음표 합계 조회
	Long sumChargedCoin(Map<String, String> param);

	// 기간 내 매출 합계 조회
	Long sumRevenue(Map<String, String> param);

	// 기간 내 사용 음표 합계 조회
	Long sumUsedCoin(Map<String, String> param);

	// 일자별 충전 음표
	List<Map<String, Object>> seriesChargedCoin(Map<String, String> param);

	// 일자별 사용 음표
	List<Map<String, Object>> seriesUsedCoin(Map<String, String> param);

	// 음표 충전 Top 10
	List<Map<String, Object>> topChargers(Map<String, String> param);

	// 음표 사용 Top 10
	List<Map<String, Object>> topSpenders(Map<String, String> param);

	// 일자별 신규 가입자
	List<Map<String, Object>> seriesNewMembers(Map<String, String> param);

	// 팔로워 Top 10
	List<Map<String, Object>> topFollowers(Map<String, String> param);

	// 음악 판매/음표 Top 10
	List<Map<String, Object>> topMusic(Map<String, String> param);

	// 일자별 수익
	List<Map<String, Object>> seriesRevenue(Map<String, String> param);

	// 시간별 방문자
	List<Map<String, Object>> seriesHourlyVisitors(Map<String, String> param);

	// 일자별 방문자
	List<Map<String, Object>> seriesDailyVisitors(Map<String, String> param);

	// 전체 이용자 수
	Long countTotalUsers();
}
