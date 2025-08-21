package com.github.musicsnsproject.web.controller.rest.eumpyo;

import com.github.musicsnsproject.service.mypage.eumpyo.EumpyoChargeService;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.github.musicsnsproject.common.security.userdetails.CustomUserDetails;

@RestController
@RequestMapping("/api/mypage/eumpyo/charge")
@RequiredArgsConstructor
public class EumpyoChargeController {

    private final EumpyoChargeService eumpyoChargeService;

    // 결제 준비
    @PostMapping("/ready")
    public Map<String, Object> ready(@AuthenticationPrincipal CustomUserDetails loginUser,
                                     @RequestBody(required = false) Map<String, Object> request,
    								 @RequestHeader(name = "X-Dev-UserId", required = false) Long devUserId)
    								 {
    	
        Map<String, Object> responseBody = new HashMap<>();

        // 로그인 사용자 확인
    //  Long userId = (loginUser != null ? loginUser.getUserId() : null);
        Long userId = (devUserId != null) ? devUserId : (loginUser != null ? loginUser.getUserId() : null);

        if (userId == null) {
        	
            responseBody.put("result", "fail");
            responseBody.put("message", "음표 충전을 위해서는 먼저 로그인을 하세요.");
            
            return responseBody;
        }

        // 바디에서 결제금액 추출
        Object amountRaw = (request == null ? null : request.get("amount"));
        
        int amount = -1;
        if (amountRaw instanceof Number) {
            amount = ((Number) amountRaw).intValue();
        } else if (amountRaw instanceof String) {
            try { amount = Integer.parseInt(((String) amountRaw).trim()); }
            catch (NumberFormatException ignore) {}
        }

        // 금액 검증 (양수 + 100원 단위)
        if (amount <= 0 || amount % 100 != 0) {
            responseBody.put("result", "fail");
            responseBody.put("message", "결제 금액이 올바르지 않습니다.");
            
            return responseBody;
        }

        // 주문번호 생성 및 임시 저장 (서비스로 위임)
        return eumpyoChargeService.requestCharge(userId, amount);
    }

    
    // 결제 완료(검증 및 적립)
    @PostMapping("/complete")
    public Map<String, Object> complete(@AuthenticationPrincipal CustomUserDetails loginUser,
                                        @RequestBody(required = false) Map<String, String> request,
       								 	@RequestHeader(name = "X-Dev-UserId", required = false) Long devUserId)
       								 	{
    	
        Map<String, Object> map = new HashMap<>();

        // 로그인 사용자 확인
    //  Long userId = (loginUser != null ? loginUser.getUserId() : null);
        Long userId = (devUserId != null) ? devUserId : (loginUser != null ? loginUser.getUserId() : null);

        if (userId == null) {
            map.put("result", "fail");
            map.put("message", "음표 충전을 위해서는 먼저 로그인을 하세요!!");
            
            return map;
        }

        String impUid = (request == null ? null : request.get("impUid"));
        String merchantUid = (request == null ? null : request.get("merchantUid"));

        // 파라미터 검증 (누락 및 공백 불가)
        if (impUid == null || impUid.isBlank() || merchantUid == null || merchantUid.isBlank()) {
            map.put("result", "fail");
            map.put("message", "요청 값이 올바르지 않습니다. 결제를 다시 진행해 주세요.");
            
            return map;
        }

        // 주문-사용자 일치 검증, 코인 적립, 잔액 조회
        return eumpyoChargeService.completeCharge(userId, impUid, merchantUid);
    }

    
    // 사용자 현재 코인 조회 (users.coin)
    @GetMapping("/balance")
    public Map<String, Object> balance(@AuthenticationPrincipal CustomUserDetails loginUser,
			 					   	   @RequestHeader(name = "X-Dev-UserId", required = false) Long devUserId)
			 						   {
    	
        Map<String, Object> map = new HashMap<>();

        // 로그인 사용자 확인
    //  Long userId = (loginUser != null ? loginUser.getUserId() : null);
        Long userId = (devUserId != null) ? devUserId : (loginUser != null ? loginUser.getUserId() : null);

        if (userId == null) {
        	map.put("result", "fail");
        	map.put("message", "로그인이 필요합니다.");
            
            return map;
        }

        long bal = eumpyoChargeService.getUserCoin(userId);
        map.put("result", "success");
        map.put("coinBalance", bal);
        
        return map;
    }
}
