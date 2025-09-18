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

	// 기간 내 충전 음표 합계 조회
	@Override
	public Long sumChargedCoin(Map<String, String> param) {
		return sql.selectOne("adminStats.sumChargedCoin", param);
	}

	// 기간 내 매출 합계 조회
	@Override
	public Long sumRevenue(Map<String, String> param) {
		return sql.selectOne("adminStats.sumRevenue", param);
	}

	// 기간 내 사용 음표 합계 조회
	@Override
	public Long sumUsedCoin(Map<String, String> param) {
		return sql.selectOne("adminStats.sumUsedCoin", param);
	}

	// 일자별 충전 음표
	@Override
	public List<Map<String, Object>> seriesChargedCoin(Map<String, String> param) {
		return sql.selectList("adminStats.seriesChargedCoin", param);
	}

	// 일자별 사용 음표
	@Override
	public List<Map<String, Object>> seriesUsedCoin(Map<String, String> param) {
		return sql.selectList("adminStats.seriesUsedCoin", param);
	}

	// 음표 충전 Top 10
	@Override
	public List<Map<String, Object>> topChargers(Map<String, String> param) {
		return sql.selectList("adminStats.topChargers", param);
	}

	// 음표 사용 Top 10
	@Override
	public List<Map<String, Object>> topSpenders(Map<String, String> param) {
		return sql.selectList("adminStats.topSpenders", param);
	}

	// 일자별 신규 가입자
	@Override
	public List<Map<String, Object>> seriesNewMembers(Map<String, String> param) {
		return sql.selectList("adminStats.seriesNewMembers", param);
	}

	// 팔로워 Top 10
	@Override
	public List<Map<String, Object>> topFollowers(Map<String, String> param) {
		return sql.selectList("adminStats.topFollowers", param);
	}

	// 음악 판매/음표 Top 10
	@Override
	public List<Map<String, Object>> topMusic(Map<String, String> param) {
		return sql.selectList("adminStats.topMusic", param);
	}

	// 일자별 수익
	@Override
	public List<Map<String, Object>> seriesRevenue(Map<String, String> param) {
		return sql.selectList("adminStats.seriesRevenue", param);
	}

	// 시간별 방문자
	@Override
	public List<Map<String, Object>> seriesHourlyVisitors(Map<String, String> param) {
		return sql.selectList("adminStats.seriesHourlyVisitors", param);
	}

	// 일자별 방문자
	@Override
	public List<Map<String, Object>> seriesDailyVisitors(Map<String, String> param) {
		return sql.selectList("adminStats.seriesDailyVisitors", param);
	}

	// 전체 이용자 수
	@Override
	public Long countTotalUsers() {
		return sql.selectOne("adminStats.countTotalUsers");
	}
}
