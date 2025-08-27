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

    private final AdminStatsService adminStatsService;

    private Map<String, String> toRangeMap(String startDate, String endDate) {
        Map<String, String> param = new HashMap<>();
        if (startDate != null && !startDate.trim().isEmpty()) param.put("startDate", startDate.trim());
        if (endDate   != null && !endDate.trim().isEmpty())   param.put("endDate",   endDate.trim());
        return param;
    }

    // 합계 요약(충전/매출/사용)
    @GetMapping("/summary")
    public Map<String, Object> summary(String startDate, String endDate) {
        Map<String, String> param = toRangeMap(startDate, endDate);
        Map<String, Object> out = new HashMap<>();
        out.put("sumChargedCoin", adminStatsService.sumChargedCoin(param));
        out.put("sumRevenue",     adminStatsService.sumRevenue(param));
        out.put("sumUsedCoin",    adminStatsService.sumUsedCoin(param));
        return out;
    }

    // 일자별 충전 음표 합계 (일 단위 고정)
    @GetMapping("/series/charged")
    public List<Map<String, Object>> seriesCharged(String startDate, String endDate) {
        return adminStatsService.seriesChargedCoin(toRangeMap(startDate, endDate));
    }

    // 일자별 사용 음표 합계 (일 단위 고정)
    @GetMapping("/series/used")
    public List<Map<String, Object>> seriesUsed(String startDate, String endDate) {
        return adminStatsService.seriesUsedCoin(toRangeMap(startDate, endDate));
    }

    // top 10 충전 유저
    @GetMapping("/top/chargers")
    public List<Map<String, Object>> topChargers(String startDate, String endDate) {
        return adminStatsService.topChargers(toRangeMap(startDate, endDate));
    }

    // top 10 사용 유저
    @GetMapping("/top/spenders")
    public List<Map<String, Object>> topSpenders(String startDate, String endDate) {
        return adminStatsService.topSpenders(toRangeMap(startDate, endDate));
    }

    // 일자별 신규 가입자 (일 단위 고정)
    @GetMapping("/series/new-members")
    public List<Map<String, Object>> seriesNewMembers(String startDate, String endDate) {
        return adminStatsService.seriesNewMembers(toRangeMap(startDate, endDate));
    }

    // 회원 상태 분포(전체)
    @GetMapping("/member/status-dist")
    public List<Map<String, Object>> memberStatusDist() {
        return adminStatsService.memberStatusDist();
    }

    // top 10 팔로워 많은 유저
    @GetMapping("/top/followers")
    public List<Map<String, Object>> topFollowers(String startDate, String endDate) {
        return adminStatsService.topFollowers(toRangeMap(startDate, endDate));
    }

    // top 10 베스트셀러 음악
    @GetMapping("/top/music")
    public List<Map<String, Object>> topMusic(String startDate, String endDate) {
        return adminStatsService.topMusic(toRangeMap(startDate, endDate));
    }
}
