package com.github.musicsnsproject.web.controller.rest.eumpyo;

import com.github.musicsnsproject.common.security.userdetails.CustomUserDetails;
import com.github.musicsnsproject.service.mypage.eumpyo.EumpyoHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/mypage/eumpyo/history")
@RequiredArgsConstructor
public class EumpyoHistoryController {

    private final EumpyoHistoryService historyService;

    private Long extractUserId(Object principal) {
    	
        if (principal == null) return null;
        if (principal instanceof Long) return (Long) principal;
        if (principal instanceof CustomUserDetails) return ((CustomUserDetails) principal).getUserId();
        
        return null;
    }

    // 충전내역 조회
    @GetMapping("/charge")
    public ResponseEntity<Map<String, Object>> chargeHistory(@AuthenticationPrincipal Object principal,
                                                             @RequestParam(defaultValue = "1") int page,
                                                             @RequestParam(defaultValue = "10") int size) {

        Long userId = extractUserId(principal);
        
        if (userId == null) {
        	
            Map<String, Object> body = new HashMap<>();
            
            body.put("result", "fail");
            body.put("message", "로그인이 필요합니다.");
            
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(body);
        }

        int safePage = Math.max(1, page);
        int safeSize = Math.max(1, size);
        
        return ResponseEntity.ok(historyService.getChargeHistory(userId, safePage, safeSize));
    }

    // 구매내역 조회
    @GetMapping("/purchase")
    public ResponseEntity<Map<String, Object>> purchaseHistory(@AuthenticationPrincipal Object principal,
                                                               @RequestParam(defaultValue = "1") int page,
                                                               @RequestParam(defaultValue = "10") int size) {

        Long userId = extractUserId(principal);
        
        if (userId == null) {
        	
            Map<String, Object> body = new HashMap<>();
            
            body.put("result", "fail");
            body.put("message", "로그인이 필요합니다.");
            
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(body);
        }

        int safePage = Math.max(1, page);
        int safeSize = Math.max(1, size);
        
        return ResponseEntity.ok(historyService.getPurchaseHistory(userId, safePage, safeSize));
    }

    // 특정 구매내역의 1건의 구매음악 상세목록
    @GetMapping("/purchase/{purchaseHistoryId}/purchaseMusic")
    public ResponseEntity<Map<String, Object>> purchaseTracks(@AuthenticationPrincipal Object principal,
                                                              @PathVariable("purchaseHistoryId") long purchaseHistoryId) {

        Long userId = extractUserId(principal);
        
        if (userId == null) {
        	
            Map<String, Object> body = new HashMap<>();
            
            body.put("result", "fail");
            body.put("message", "로그인이 필요합니다.");
            
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(body);
        }

        return ResponseEntity.ok(historyService.getPurchaseMusic(userId, purchaseHistoryId));
    }
}
