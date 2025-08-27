package com.github.musicsnsproject.service.mypage.eumpyo;

import com.github.musicsnsproject.repository.mybatis.dao.eumpyo.EumpyoChargeDAO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class EumpyoChargeService_imple implements EumpyoChargeService {

    private final EumpyoChargeDAO eumpyoChargeDAO;

    // 결제 준비
    // 주문번호 임시 저장소 (중복 처리 방지)
    private final Map<String, OrderTemp> orderTempByMerchantUid = new ConcurrentHashMap<>();

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

        if (amount <= 0 || amount % 100 != 0) {
            map.put("result", "fail");
            map.put("message", "선택한 금액이 올바르지 않습니다.");
            
            return map;
        }

        String merchantUid = "ORD-" + userId + "-" + System.currentTimeMillis();

        orderTempByMerchantUid.put(merchantUid, new OrderTemp(userId, amount));

        int chargedCoin = convertAmountToCoin(amount);

        map.put("result", "success");
        map.put("merchantUid", merchantUid);
        map.put("amountKRW", amount);
        map.put("chargedCoin", chargedCoin);
        
        return map;
    }

    // 결제 확정(검증)
    @Override
    @Transactional
    public Map<String, Object> completeCharge(long userId, String impUid, String merchantUid) {
    	
        Map<String, Object> map = new HashMap<>();

        // 임시 주문 건 조회(동시에 제거해 중복처리 방지)
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
            int atThatPrice = orderTemp.expectedAmount;
            int coin = convertAmountToCoin(atThatPrice);
            if (coin <= 0) {
                map.put("result", "fail");
                map.put("message", "결제 금액을 확인할 수 없습니다.");
                
                return map;
            }

            // 결제 검증 통과 처리
            boolean ok = verifyPayment(impUid, merchantUid, atThatPrice);
            
            if (!ok) {
                map.put("result", "fail");
                map.put("message", "결제 검증에 실패했습니다. 관리자에게 문의해 주세요.");
                return map;
            }

            // 사용자 음표 조회(잠금/동시수정불가)
            Long before = eumpyoChargeDAO.selectUserCoinForUpdate(userId);
            long beforeBalance = (before == null ? 0L : before);
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
    
    private boolean verifyPayment(String impUid, String merchantUid, int expectedAmount) {
        return true;
    }
    
}
