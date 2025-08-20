package com.github.musicsnsproject.web.controller.rest.eumpyo;

import com.github.musicsnsproject.common.security.userdetails.CustomUserDetails;
import com.github.musicsnsproject.service.mypage.eumpyo.EumpyoHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/mypage/eumpyo/history")
@RequiredArgsConstructor
public class EumpyoHistoryController {

    private final EumpyoHistoryService historyService;

    // 충전내역 조회
    @GetMapping("/charge")
    public Map<String, Object> chargeHistory(@AuthenticationPrincipal CustomUserDetails loginUser,
                                             @RequestParam(defaultValue = "1") int page,
                                             @RequestParam(defaultValue = "10") int size) {
    	
        Long userId = (loginUser != null ? loginUser.getUserId() : null);

        if (userId == null) {
            Map<String, Object> fail = new HashMap<>();
            fail.put("result", "fail");
            fail.put("message", "로그인이 필요합니다.");
            
            return fail;
        }

        int safePage = Math.max(1, page);
        int safeSize = Math.max(1, size);

        return historyService.getChargeHistory(userId, safePage, safeSize);
    }

    // 구매내역 조회
    @GetMapping("/purchase")
    public Map<String, Object> purchaseHistory(@AuthenticationPrincipal CustomUserDetails loginUser,
                                               @RequestParam(defaultValue = "1") int page,
                                               @RequestParam(defaultValue = "10") int size) {
    	
        Long userId = (loginUser != null ? loginUser.getUserId() : null);

        if (userId == null) {
            Map<String, Object> fail = new HashMap<>();
            fail.put("result", "fail");
            fail.put("message", "로그인이 필요합니다.");
            
            return fail;
        }

        int safePage = Math.max(1, page);
        int safeSize = Math.max(1, size);

        return historyService.getPurchaseHistory(userId, safePage, safeSize);
    }
}
