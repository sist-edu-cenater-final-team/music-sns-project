package com.github.musicsnsproject.web.controller.view.admin;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminStatsViewController {
	
    // 관리자 통계 메인 화면
    @GetMapping("/stats")
    public String stats() {
        return "admin/stats";
    }
    
 }
