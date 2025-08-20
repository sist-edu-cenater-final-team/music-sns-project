package com.github.musicsnsproject.service.mypage.eumpyo;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.musicsnsproject.repository.mybatis.dao.eumpyo.EumpyoPurchaseDAO;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EumpyoPurchaseService_imple implements EumpyoPurchaseService {

    private final EumpyoPurchaseDAO purchaseDAO;

    
    // 음원 구매 처리
    @Override
    @Transactional
    public Map<String, Object> purchase(long userId, String musicId, int usedCoin) {
    	
        Map<String, Object> map = new HashMap<>();
        
        if (userId <= 0L) {
        	map.put("result","fail");
        	map.put("message","유효하지 않은 사용자입니다.");
        	
        	return map;
        }
        
        if (musicId == null || musicId.isBlank()) {
        	map.put("result","fail");
        	map.put("message","유효하지 않은 음악 정보입니다.");
        	
        	return map;
        }
        
        if (usedCoin <= 0) {
        	map.put("result","fail");
        	map.put("message","사용할 음표 수량이 올바르지 않습니다.");
        	
        	return map;
        }

        // 코인 차감
        int updated = purchaseDAO.decreaseUserCoin(userId, usedCoin);
        
        if (updated == 0) {
        	
            Long balance = purchaseDAO.selectUserCoin(userId);
            
            map.put("result","fail");
            map.put("message","보유 음표가 부족합니다.");
            map.put("coinBalance", balance == null ? 0L : balance);
            
            return map;
        }

        
        // 구매 내역 기록
        int inserted = purchaseDAO.insertPurchaseHistory(userId, musicId, usedCoin);
        
        if (inserted == 0) {
        	throw new IllegalStateException("purchase_history insert 실패");
        }

        // 최신 잔액 조회 
        Long balance = purchaseDAO.selectUserCoin(userId);
        
        map.put("result","success");
        map.put("message","구매가 완료되었습니다.");
        map.put("usedCoin", usedCoin);
        map.put("coinBalance", balance == null ? 0L : balance);
        map.put("musicId", musicId);
        
        return map;
    }

    
    // 사용자 코인 재정산
    @Override
    @Transactional
    public int recalcUserCoin(long userId) {
        if (userId <= 0L) return 0;
        return purchaseDAO.recalcUserCoinFromHistory(userId);
    }

}
