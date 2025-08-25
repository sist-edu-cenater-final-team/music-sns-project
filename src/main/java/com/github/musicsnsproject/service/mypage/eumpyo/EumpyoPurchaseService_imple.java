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

        // 구매 직전 잔액 조회
        Long before = purchaseDAO.selectUserCoin(userId);
        long beforeBal = (before == null ? 0L : before);

        // [CHANGE] 사전 잔액 검증 - 부족하면 실패 처리
        if (beforeBal < atThatCoin) {
            res.put("result", "fail");
            res.put("message", "보유 음표가 부족합니다.");
            res.put("coinBalance", beforeBal);
            return res;
        }

        long afterBal  = beforeBal - atThatCoin; // 구매 후 예상 잔액

        // 구매내역 생성
        Map<String, Object> ph = new HashMap<>();
        ph.put("userId", userId);
        ph.put("atThatUserCoin", afterBal);

        int insertedHistory = purchaseDAO.insertPurchaseHistory(ph);
        if (insertedHistory == 0 || ph.get("purchaseHistoryId") == null) {
            throw new IllegalStateException("purchase_history insert 실패");
        }

        long purchaseHistoryId = ((Number)ph.get("purchaseHistoryId")).longValue();

        // 구매음악 1행 추가
        Map<String, Object> pm = new HashMap<>();
        pm.put("purchaseHistoryId", purchaseHistoryId);
        pm.put("musicId", musicId);
        pm.put("usedCoin", atThatCoin);

        purchaseDAO.insertPurchaseMusic(pm);

        // 사용자 음표 재정산 (충전-구매 합산)
        purchaseDAO.recalcUserCoinFromHistory(userId);

        // 최신 코인 조회
        Long balance = purchaseDAO.selectUserCoin(userId);

        res.put("result", "success");
        res.put("purchaseHistoryId", purchaseHistoryId);
        res.put("musicId", musicId);
        res.put("usedCoin", atThatCoin);
        res.put("coinBalance", balance == null ? 0L : balance);
        return res;
    }

    // 여러 노래 한꺼번에 구매 기록 (1건의 purchase_history + 여러 purchase_music)
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

        // 유효 항목 필터링 및 합계
        int totalUsedCoin = 0;
        for (PurchaseItem it : items) {
            if (it == null) continue;
            if (it.getMusicId() == null || it.getMusicId().isBlank()) continue;
            if (it.getAtThatCoin() <= 0) continue;
            totalUsedCoin += it.getAtThatCoin();
        }

        if (totalUsedCoin <= 0) {
            map.put("result","fail");
            map.put("message","유효한 구매 항목이 없음");
            return map;
        }

        // 구매 직전 잔액 → 구매 후 잔액
        Long before = purchaseDAO.selectUserCoin(userId);
        long beforeBal = (before == null ? 0L : before);

        // [CHANGE] 사전 잔액 검증 - 부족하면 실패 처리
        if (beforeBal < totalUsedCoin) {
            map.put("result", "fail");
            map.put("message", "보유 음표가 부족합니다.");
            map.put("coinBalance", beforeBal);
            map.put("totalRequiredCoin", totalUsedCoin);
            return map;
        }

        long afterBal  = beforeBal - totalUsedCoin;

        // 구매내역 1건 생성(총 사용음표 기준 잔액)
        Map<String, Object> ph = new HashMap<>();
        ph.put("userId", userId);
        ph.put("atThatUserCoin", afterBal);

        int insertedHistory = purchaseDAO.insertPurchaseHistory(ph);
        if (insertedHistory == 0 || ph.get("purchaseHistoryId") == null) {
            throw new IllegalStateException("purchase_history insert 실패");
        }
        long purchaseHistoryId = ((Number)ph.get("purchaseHistoryId")).longValue();

        // 각 곡을 구매음악에 추가
        int saved = 0;
        for (PurchaseItem it : items) {
            if (it == null) continue;
            if (it.getMusicId() == null || it.getMusicId().isBlank()) continue;
            if (it.getAtThatCoin() <= 0) continue;

            Map<String, Object> pm = new HashMap<>();
            pm.put("purchaseHistoryId", purchaseHistoryId);
            pm.put("musicId", it.getMusicId());
            pm.put("usedCoin", it.getAtThatCoin());
            saved += purchaseDAO.insertPurchaseMusic(pm);
        }

        // 사용자 음표 재산출
        purchaseDAO.recalcUserCoinFromHistory(userId);

        // 최신 잔액 조회
        Long balance = purchaseDAO.selectUserCoin(userId);

        map.put("result", "success");
        map.put("purchaseHistoryId", purchaseHistoryId);
        map.put("savedCount", saved);
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