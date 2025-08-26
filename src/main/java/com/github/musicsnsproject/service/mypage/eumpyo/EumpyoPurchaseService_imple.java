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

        Map<String, Object> resultMap = new HashMap<>();

        // 기본 검증
        if (userId <= 0L) {
            resultMap.put("result", "fail");
            resultMap.put("message", "잘못된 사용자");
            return resultMap;
        }
        
        if (musicId == null || musicId.trim().isEmpty()) {
            resultMap.put("result", "fail");
            resultMap.put("message", "잘못된 음악 ID");
            return resultMap;
        }
        
        if (atThatCoin <= 0) {
            resultMap.put("result", "fail");
            resultMap.put("message", "음표 수량 오류");
            return resultMap;
        }

        // 현재 사용자 코인 조회 (잠금/동시수정불가)
        Long before = purchaseDAO.selectUserCoinForUpdate(userId);
        long beforeBalance = (before == null ? 0L : before);

        // 사전 잔액 검증
        if (beforeBalance < atThatCoin) {
            resultMap.put("result", "fail");
            resultMap.put("message", "보유 음표가 부족합니다.");
            resultMap.put("coinBalance", beforeBalance);
            return resultMap;
        }

        long afterBalance = beforeBalance - atThatCoin; // 구매 후 예상 잔액

        // 구매내역 생성
        Map<String, Object> historyParam = new HashMap<>();
        
        historyParam.put("userId", userId);
        historyParam.put("atThatUserCoin", afterBalance);

        int insertedHistory = purchaseDAO.insertPurchaseHistory(historyParam);
        if (insertedHistory == 0 || historyParam.get("purchaseHistoryId") == null) {
            resultMap.put("result", "fail");
            resultMap.put("message", "구매내역 저장에 실패했습니다.");
            return resultMap;
        }

        long purchaseHistoryId = ((Number) historyParam.get("purchaseHistoryId")).longValue();

        // 구매음악 1행 추가
        Map<String, Object> musicParam = new HashMap<>();
        
        musicParam.put("purchaseHistoryId", purchaseHistoryId);
        musicParam.put("musicId", musicId);
        musicParam.put("usedCoin", atThatCoin);
        purchaseDAO.insertPurchaseMusic(musicParam);

        // 사용자 음표 재정산 (충전-구매 합산)
        purchaseDAO.recalcUserCoinFromHistory(userId);

        // 최신 음표 조회
        Long balance = purchaseDAO.selectUserCoin(userId);

        resultMap.put("result", "success");
        resultMap.put("purchaseHistoryId", purchaseHistoryId);
        resultMap.put("musicId", musicId);
        resultMap.put("usedCoin", atThatCoin);
        resultMap.put("coinBalance", balance == null ? 0L : balance);
        return resultMap;
    }

    // 여러 노래 한꺼번에 구매 기록 (1건의 purchase_history + 여러 purchase_music)
    @Override
    @Transactional
    public Map<String, Object> purchaseBulk(long userId, List<PurchaseItem> items) {
    	
        Map<String, Object> resultMap = new HashMap<>();

        if (userId <= 0L) {
            resultMap.put("result", "fail");
            resultMap.put("message", "잘못된 사용자");
            return resultMap;
        }
        
        if (items == null || items.isEmpty()) {
            resultMap.put("result", "fail");
            resultMap.put("message", "구매할 항목이 없음");
            return resultMap;
        }

        // 유효 항목 필터링 및 합계
        int totalUsedCoin = 0;
        for (PurchaseItem it : items) {
            if (it == null) continue;
            if (it.getMusicId() == null || it.getMusicId().isBlank()) continue;
            if (it.getAtThatCoin() <= 0) continue;
            totalUsedCoin += it.getAtThatCoin();
        }
        if (totalUsedCoin <= 0) {
            resultMap.put("result", "fail");
            resultMap.put("message", "유효한 구매 항목이 없음");
            return resultMap;
        }

        // 현재 사용자 코인 조회 (잠금/동시수정불가)
        Long before = purchaseDAO.selectUserCoinForUpdate(userId);
        long beforeBalance = (before == null ? 0L : before);

        if (beforeBalance < totalUsedCoin) {
            resultMap.put("result", "fail");
            resultMap.put("message", "보유 음표가 부족합니다.");
            resultMap.put("coinBalance", beforeBalance);
            resultMap.put("totalRequiredCoin", totalUsedCoin);
            return resultMap;
        }

        long afterBalance = beforeBalance - totalUsedCoin;

        // 구매내역 1건 생성
        Map<String, Object> historyParam = new HashMap<>();
        
        historyParam.put("userId", userId);
        historyParam.put("atThatUserCoin", afterBalance);

        int insertedHistory = purchaseDAO.insertPurchaseHistory(historyParam);
        if (insertedHistory == 0 || historyParam.get("purchaseHistoryId") == null) {
            resultMap.put("result", "fail");
            resultMap.put("message", "구매내역 저장에 실패했습니다.");
            return resultMap;
        }
        long purchaseHistoryId = ((Number) historyParam.get("purchaseHistoryId")).longValue();

        // 각 곡을 구매음악에 추가
        int saved = 0;
        for (PurchaseItem it : items) {
            if (it == null) continue;
            if (it.getMusicId() == null || it.getMusicId().isBlank()) continue;
            if (it.getAtThatCoin() <= 0) continue;

            Map<String, Object> musicParam = new HashMap<>();
            
            musicParam.put("purchaseHistoryId", purchaseHistoryId);
            musicParam.put("musicId", it.getMusicId());
            musicParam.put("usedCoin", it.getAtThatCoin());
            saved += purchaseDAO.insertPurchaseMusic(musicParam);
        }

        // 사용자 음표 재산출
        purchaseDAO.recalcUserCoinFromHistory(userId);

        // 최신 잔액 조회
        Long balance = purchaseDAO.selectUserCoin(userId);

        resultMap.put("result", "success");
        resultMap.put("purchaseHistoryId", purchaseHistoryId);
        resultMap.put("savedCount", saved);
        resultMap.put("totalUsedCoin", totalUsedCoin);
        resultMap.put("coinBalance", balance == null ? 0L : balance);
        return resultMap;
    }

    // 사용자 음표 재정산
    @Override
    @Transactional
    public int recalcUserCoin(long userId) {
        if (userId <= 0L) return 0;     
        try {
            purchaseDAO.recalcUserCoinFromHistory(userId); 
            return 1; // 성공
        } catch (Exception ignore) {
            return 0; // 실패
        }
    }
}
