package com.github.musicsnsproject.service.mypage.eumpyo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.musicsnsproject.repository.mybatis.dao.eumpyo.EumpyoPurchaseDAO;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EumpyoPurchaseService_imple implements EumpyoPurchaseService {

    private final EumpyoPurchaseDAO purchaseDAO;

    // 노래 1개 구매 기록
    @Override
    @Transactional
    public Map<String, Object> purchase(long userId, String musicId, int atThatCoin) {
        Map<String, Object> res = new HashMap<>();

        if (userId <= 0L) { res.put("result","fail"); res.put("message","잘못된 사용자"); return res; }
        if (musicId == null || musicId.trim().isEmpty()) { res.put("result","fail"); res.put("message","잘못된 음악 ID"); return res; }
        if (atThatCoin <= 0) { res.put("result","fail"); res.put("message","음표 수량 오류"); return res; }

        // 구매 내역 1건 기록
        int inserted = purchaseDAO.insertPurchaseHistory(userId, musicId, atThatCoin);
        if (inserted == 0) throw new IllegalStateException("purchase_history insert 실패");

        // 보유 코인 조회(없으면 0)
        Long balance = purchaseDAO.selectUserCoin(userId);

        res.put("result", "success");
        res.put("musicId", musicId);
        res.put("usedCoin", atThatCoin);
        res.put("coinBalance", balance == null ? 0L : balance);
        return res;
    }

    // 여러 노래 한꺼번에 구매 기록
    @Override
    @Transactional
    public Map<String, Object> purchaseBulk(long userId, List<PurchaseItem> items) {
        Map<String, Object> map = new HashMap<>();

        if (userId <= 0L) {
        
        	map.put("result","fail");
        	map.put("message","잘못된 사용자");
        	
        	return map;
        }
        
        if (items == null || items.isEmpty()) {
        	
        	map.put("result","fail");
        	map.put("message","구매할 항목이 없음");
        	
        	return map;
        }

        int success = 0;       // 성공 건수
        int totalUsedCoin = 0; // 총 사용 음표(표시용)
        for (PurchaseItem it : items) {
            if (it == null) continue;
            String musicId = it.getMusicId();
            int atThatCoin = it.getAtThatCoin();
            if (musicId == null || musicId.trim().isEmpty()) continue;
            if (atThatCoin <= 0) continue;

            // 구매 내역 1건 기록
            int inserted = purchaseDAO.insertPurchaseHistory(userId, musicId, atThatCoin);
            success += inserted;
            if (inserted > 0) totalUsedCoin += atThatCoin;
        }

        // 보유 코인 조회(없으면 0)
        Long balance = purchaseDAO.selectUserCoin(userId);

        map.put("result", "success");
        map.put("savedCount", success);
        map.put("totalUsedCoin", totalUsedCoin);
        map.put("coinBalance", balance == null ? 0L : balance);
        
        return map;
    }

    // 사용자 음표 재정산 (충전/구매 내역 기준)
    @Override
    @Transactional
    public int recalcUserCoin(long userId) {
        if (userId <= 0L) return 0;
        return purchaseDAO.recalcUserCoinFromHistory(userId);
    }
}
