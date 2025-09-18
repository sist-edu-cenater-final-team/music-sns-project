package com.github.musicsnsproject.web.controller.rest.eumpyo;

import com.github.musicsnsproject.common.security.userdetails.CustomUserDetails;
import com.github.musicsnsproject.service.mypage.eumpyo.EumpyoChargeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/mypage/eumpyo/charge")
@RequiredArgsConstructor
public class EumpyoChargeController {

    private final EumpyoChargeService eumpyoChargeService;

    private Long extractUserId(Object principal) {
    	
        if (principal == null) return null;
        if (principal instanceof Long) return (Long) principal;
        if (principal instanceof CustomUserDetails) return ((CustomUserDetails) principal).getUserId();
        
        return null;
    }

    @PostMapping("/ready")
    public ResponseEntity<Map<String, Object>> ready(@AuthenticationPrincipal Object principal,
                                                     @RequestBody(required = false) Map<String, Object> request) {

        Long userId = extractUserId(principal);
        
        if (userId == null) {
        	
            Map<String, Object> body = new HashMap<>();
            
            body.put("result", "fail");
            body.put("message", "로그인이 필요합니다.");
            
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(body);
        }

        Object amountRaw = (request == null ? null : request.get("amount"));
        
        int amount = -1;
        
        if (amountRaw instanceof Number) {
            amount = ((Number) amountRaw).intValue();
        } else if (amountRaw instanceof String) {
            try { amount = Integer.parseInt(((String) amountRaw).trim()); }
            catch (NumberFormatException ignore) {}
        }

        if (amount <= 0 || amount % 100 != 0) {
        	
            Map<String, Object> body = new HashMap<>();
            
            body.put("result", "fail");
            body.put("message", "결제 금액이 올바르지 않습니다.");
            
            return ResponseEntity.badRequest().body(body);
        }

        return ResponseEntity.ok(eumpyoChargeService.requestCharge(userId, amount));
    }

    @PostMapping("/complete")
    public ResponseEntity<Map<String, Object>> complete(@AuthenticationPrincipal Object principal,
                                                        @RequestBody(required = false) Map<String, String> request) {

        Long userId = extractUserId(principal);
        
        if (userId == null) {
        	
            Map<String, Object> body = new HashMap<>();
            
            body.put("result", "fail");
            body.put("message", "로그인이 필요합니다.");
            
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(body);
        }

        String impUid = (request == null ? null : request.get("impUid"));
        String merchantUid = (request == null ? null : request.get("merchantUid"));

        if (impUid == null || impUid.isBlank() || merchantUid == null || merchantUid.isBlank()) {
        	
            Map<String, Object> body = new HashMap<>();
            
            body.put("result", "fail");
            body.put("message", "요청 값이 올바르지 않습니다. 결제를 다시 진행해 주세요.");
            
            return ResponseEntity.badRequest().body(body);
        }

        return ResponseEntity.ok(eumpyoChargeService.completeCharge(userId, impUid, merchantUid));
    }

    /** 현재 코인 잔액 */
    @GetMapping("/balance")
    public ResponseEntity<Map<String, Object>> balance(@AuthenticationPrincipal Object principal) {
    	
        Long userId = extractUserId(principal);
        
        if (userId == null) {
        	
            Map<String, Object> body = new HashMap<>();
            
            body.put("result", "fail");
            body.put("message", "로그인이 필요합니다.");
            
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(body);
        }

        long bal = eumpyoChargeService.getUserCoin(userId);
        
        Map<String, Object> body = new HashMap<>();
        
        body.put("result", "success");
        body.put("coinBalance", bal);
        
        return ResponseEntity.ok(body);
    }

    // 구매자 정보
    @GetMapping("/buyer")
    public ResponseEntity<Map<String, Object>> buyer(@AuthenticationPrincipal Object principal) {
    	
        if (!(principal instanceof CustomUserDetails)) {
        	
            Map<String, Object> body = new HashMap<>();
            
            body.put("result", "fail");
            body.put("message", "로그인이 필요합니다.");
            
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(body);
        }

        CustomUserDetails user = (CustomUserDetails) principal;

        String name  = user.getNickname() != null ? user.getNickname() : "";
        String email = user.getEmail()    != null ? user.getEmail()    : "";

        String phoneNumber = "";

        Map<String, Object> body = new HashMap<>();
        
        body.put("result", "success");
        body.put("name", name);
        body.put("email", email);
        body.put("phoneNumber", phoneNumber);
        
        return ResponseEntity.ok(body);
    }
}
