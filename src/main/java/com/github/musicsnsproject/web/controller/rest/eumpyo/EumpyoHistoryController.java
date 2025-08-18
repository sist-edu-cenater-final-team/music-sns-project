package com.github.musicsnsproject.web.controller.rest.eumpyo;

import com.github.musicsnsproject.common.security.userdetails.CustomUserDetails;
import com.github.musicsnsproject.service.mypage.eumpyo.EumpyoHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/mypage/eumpyo/history")
@RequiredArgsConstructor
public class EumpyoHistoryController {

    private final EumpyoHistoryService historyService;

    // 충전 내역 조회
    @GetMapping("/charge")
    public Map<String, Object> chargeHistory(@AuthenticationPrincipal CustomUserDetails loginUser,
                                             @RequestParam(defaultValue = "1") int page,
                                             @RequestParam(defaultValue = "10") int size) {

        if (loginUser == null) {
            Map<String, Object> fail = new HashMap<>();
            fail.put("result", "fail");
            fail.put("message", "로그인이 필요합니다.");
            return fail;
        }

        Long userId = loginUser.getUserId();
        return historyService.getChargeHistory(userId, page, size);
    }

    // 사용 내역 조회
    @GetMapping("/use")
    public Map<String, Object> useHistory(@AuthenticationPrincipal CustomUserDetails loginUser,
                                          @RequestParam(defaultValue = "1") int page,
                                          @RequestParam(defaultValue = "10") int size) {

        if (loginUser == null) {
            Map<String, Object> fail = new HashMap<>();
            fail.put("result", "fail");
            fail.put("message", "로그인이 필요합니다.");
            return fail;
        }

        Long userId = loginUser.getUserId();
        return historyService.getUseHistory(userId, page, size);
    }
}