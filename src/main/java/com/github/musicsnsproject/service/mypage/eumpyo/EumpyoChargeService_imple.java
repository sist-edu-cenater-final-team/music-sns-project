package com.github.musicsnsproject.service.mypage.eumpyo;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import com.github.musicsnsproject.repository.mybatis.dao.eumpyo.EumpyoChargeDAO;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EumpyoChargeService_imple implements EumpyoChargeService {

    private final EumpyoChargeDAO eumpyoChargeDAO;

    // 결제 준비
    // 주문번호 임시 주문 저장소
    private final Map<String, OrderTemp> orderTempByMerchantUid = new HashMap<>();

    
    // 임시 주문 객체 (userId, 예상 결제금액(원))
    private static final class OrderTemp {
        final long userId;
        final int expectedAmount;
        OrderTemp(long userId, int expectedAmount) {
            this.userId = userId;
            this.expectedAmount = expectedAmount;
        }
    }

    
    // 금액에서 코인 환산 (100원 = 1코인)
    private int convertAmountToCoin(int amount) {
        if (amount <= 0) return 0;
        if (amount % 100 != 0) return 0;
        
        return amount / 100;
    }

    
    // 충전 요청 생성 (주문번호 발급 + 예상 코인 계산)
    @Override
    public Map<String, Object> requestCharge(long userId, int amount) {
        Map<String, Object> map = new HashMap<>();

        // 100원 단위 검증
        if (amount <= 0 || amount % 100 != 0) {
            map.put("result", "fail");
            map.put("message", "선택한 금액이 올바르지 않습니다.");
            
            return map;
        }

        // 주문번호 생성
        String merchantUid = "ORD-" + userId + "-" + System.currentTimeMillis();
        orderTempByMerchantUid.put(merchantUid, new OrderTemp(userId, amount));

        int chargedCoin = convertAmountToCoin(amount);

        map.put("result", "success");
        map.put("merchantUid", merchantUid);
        map.put("amountKRW", amount);   // 실제 결제 금액(원)
        map.put("chargedCoin", chargedCoin); // 충전 음표(개)
        
        return map;
    }

    
    // 결제 확정(검증)
    @Override
    @Transactional
    public Map<String, Object> completeCharge(long userId, String impUid, String merchantUid) {
        Map<String, Object> map = new HashMap<>();

        // 중복 처리 방지: 먼저 remove → 같은 주문번호 재호출 시 null 반환되어 차단
        OrderTemp orderTemp = orderTempByMerchantUid.remove(merchantUid); 
        if (orderTemp == null) {
            map.put("result", "fail");
            map.put("message", "이미 처리되었거나 유효하지 않은 요청입니다.");
            return map;
        }
        if (orderTemp.userId != userId) {
            map.put("result", "fail");
            map.put("message", "회원 정보가 일치하지 않습니다.");
            
            return map;
        }

        try {
            int atThatPrice = orderTemp.expectedAmount;  // 결제금액(원)
            int coin = convertAmountToCoin(atThatPrice); // 100원=1음표
            if (coin <= 0) {
                map.put("result", "fail");
                map.put("message", "결제 금액을 확인할 수 없습니다.");
                return map;
            }
            
            // 사용자 음표 조회(잠금/동시수정불가)
            Long before = eumpyoChargeDAO.selectUserCoinForUpdate(userId);

            // 거래 전 음표
            long beforeBalance = (before == null ? 0L : before);
            
            // 거래 후 음표
            long afterBalance  = beforeBalance + coin;
            
            // 충전 이력 저장(충전 후 잔액 포함)
            int ins = eumpyoChargeDAO.insertChargeHistory(userId, coin, atThatPrice, afterBalance);
            if (ins != 1) {
                throw new IllegalStateException("coin_history insert failed");
            }

            // 최종 잔액 그대로 저장
            int upd = eumpyoChargeDAO.setUserCoin(userId, afterBalance);
            if (upd != 1) {
                throw new IllegalStateException("users coin update failed");   
            }
            
            map.put("result", "success");
            map.put("amount", atThatPrice);
            map.put("chargedCoin", coin);
            map.put("coinBalance", afterBalance);
            map.put("message", "충전이 완료되었습니다.");
            
            return map;

        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            map.put("result", "fail");
            map.put("message", "처리 중 오류가 발생했습니다.");
            
            return map;
        }
    }

    
    // 보유 음표
    @Override
    @Transactional(readOnly = true)
    public Long getUserCoin(long userId) {
    	
        Long coin = eumpyoChargeDAO.selectUserCoin(userId);
        
        return (coin == null ? 0L : coin);
    }
}
