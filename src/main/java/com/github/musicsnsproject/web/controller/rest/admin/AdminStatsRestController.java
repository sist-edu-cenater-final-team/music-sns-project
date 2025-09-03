package com.github.musicsnsproject.web.controller.rest.admin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.github.musicsnsproject.service.admin.AdminStatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/stats")
@RequiredArgsConstructor
public class AdminStatsRestController {

	private final AdminStatsService svc;

	// 날짜 파라미터 구성
	private Map<String, String> toRange(String s, String e){
		Map<String, String> m = new HashMap<>();
		if (s != null && !s.isBlank()) m.put("startDate", s.trim());
		if (e != null && !e.isBlank()) m.put("endDate", e.trim());
		return m;
	}

	// 합계 요약(충전/매출/사용)
	@GetMapping("/summary")
	public Map<String,Object> summary(String startDate, String endDate){
		Map<String,String> p = toRange(startDate,endDate);
		Map<String,Object> out = new HashMap<>();
		out.put("sumChargedCoin", svc.sumChargedCoin(p));
		out.put("sumRevenue", svc.sumRevenue(p));
		out.put("sumUsedCoin", svc.sumUsedCoin(p));
		return out;
	}

	// 일자별 충전 음표
	@GetMapping("/series/charged")
	public List<Map<String,Object>> seriesCharged(String startDate, String endDate){
		return svc.seriesChargedCoin(toRange(startDate,endDate));
	}

	// 일자별 사용 음표
	@GetMapping("/series/used")
	public List<Map<String,Object>> seriesUsed(String startDate, String endDate){
		return svc.seriesUsedCoin(toRange(startDate,endDate));
	}

	// 음표 충전 Top 10
	@GetMapping("/top/chargers")
	public List<Map<String,Object>> topChargers(String startDate, String endDate){
		return svc.topChargers(toRange(startDate,endDate));
	}

	// 음표 사용 Top 10
	@GetMapping("/top/spenders")
	public List<Map<String,Object>> topSpenders(String startDate, String endDate){
		return svc.topSpenders(toRange(startDate,endDate));
	}

	// 일자별 신규 가입자
	@GetMapping("/series/new-members")
	public List<Map<String,Object>> seriesNewMembers(String startDate, String endDate){
		return svc.seriesNewMembers(toRange(startDate,endDate));
	}

	// 팔로워 Top 10
	@GetMapping("/top/followers")
	public List<Map<String,Object>> topFollowers(String startDate, String endDate){
		return svc.topFollowers(toRange(startDate,endDate));
	}

	// 음악 판매/음표 Top 10
	@GetMapping("/top/music")
	public List<Map<String,Object>> topMusic(String startDate, String endDate){
		return svc.topMusic(toRange(startDate,endDate));
	}

	// 일자별 수익
	@GetMapping("/series/revenue")
	public List<Map<String,Object>> seriesRevenue(String startDate, String endDate){
		return svc.seriesRevenue(toRange(startDate,endDate));
	}

	// 시간별 방문자
	@GetMapping("/visits/hourly")
	public List<Map<String,Object>> hourlyVisitors(String startDate, String endDate){
		return svc.seriesHourlyVisitors(toRange(startDate,endDate));
	}

	// 일자별 방문자
	@GetMapping("/visits/daily")
	public List<Map<String,Object>> dailyVisitors(String startDate, String endDate){
		return svc.seriesDailyVisitors(toRange(startDate,endDate));
	}

	// 전체 이용자 수
	@GetMapping("/visits/summary")
	public Map<String,Object> visitsSummary(){
		Map<String,Object> out = new HashMap<>();
		out.put("totalUsers", svc.countTotalUsers());
		return out;
	}
}
