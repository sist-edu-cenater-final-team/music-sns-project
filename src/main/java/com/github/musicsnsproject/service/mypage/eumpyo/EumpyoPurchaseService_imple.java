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

        // 곡당 1음표 고정
        int usedCoinPerTrack = 1;

        // 현재 사용자 코인 조회 (잠금/동시수정불가)
        Long before = purchaseDAO.selectUserCoinForUpdate(userId);
        long beforeBalance = (before == null ? 0L : before);

        if (beforeBalance < usedCoinPerTrack) {
            resultMap.put("result", "fail");
            resultMap.put("message", "보유 음표가 부족합니다.");
            resultMap.put("coinBalance", beforeBalance);
            return resultMap;
        }

        // 구매내역 저장 (초기: 구매 후 잔액 NULL)
        Map<String, Object> historyParam = new HashMap<>();
        historyParam.put("userId", userId);
        int insertedHistory = purchaseDAO.insertPurchaseHistory(historyParam);
        if (insertedHistory == 0 || historyParam.get("purchaseHistoryId") == null) {
            resultMap.put("result", "fail");
            resultMap.put("message", "구매내역 저장 실패");
            return resultMap;
        }
        long purchaseHistoryId = ((Number) historyParam.get("purchaseHistoryId")).longValue();

        // 구매음악 1행 추가 (곡당 1음표)
        Map<String, Object> musicParam = new HashMap<>();
        musicParam.put("purchaseHistoryId", purchaseHistoryId);
        musicParam.put("musicId", musicId);
        purchaseDAO.insertPurchaseMusic(musicParam);

        // 이번 건만 반영: users.coin에서 1 차감
        Map<String, Object> dec = new HashMap<>();
        dec.put("userId", userId);
        dec.put("delta", usedCoinPerTrack);
        purchaseDAO.decreaseUserCoin(dec);

        // 구매 후 잔액
        long afterBalance;
        Number afterCoinOut = (Number) dec.get("afterCoin"); // ★ DB RETURNING coin
        if (afterCoinOut != null) afterBalance = afterCoinOut.longValue();
        else afterBalance = beforeBalance - usedCoinPerTrack; // fallback

        // 구매내역 행에 확정 잔액 반영
        Map<String, Object> upd = new HashMap<>();
        upd.put("purchaseHistoryId", purchaseHistoryId);
        upd.put("userId", userId);
        upd.put("atThatUserCoin", afterBalance); // ★ DB가 준 값 우선
        purchaseDAO.updatePurchaseHistoryBalance(upd);

        resultMap.put("result", "success");
        resultMap.put("purchaseHistoryId", purchaseHistoryId);
        resultMap.put("musicId", musicId);
        resultMap.put("usedCoin", usedCoinPerTrack);
        resultMap.put("coinBalance", afterBalance);

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

        // 유효 항목 필터링 + 총 사용량
        int totalUsedCoin = 0;
        for (PurchaseItem it : items) {
            if (it == null) continue;
            if (it.getMusicId() == null || it.getMusicId().isBlank()) continue;
            if (it.getAtThatCoin() <= 0) continue;
            totalUsedCoin += 1;     // 곡당 1
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

        // 구매내역 1건 생성 (초기: 구매 후 잔액 NULL)
        Map<String, Object> historyParam = new HashMap<>();
        historyParam.put("userId", userId);
        int insertedHistory = purchaseDAO.insertPurchaseHistory(historyParam);
        if (insertedHistory == 0 || historyParam.get("purchaseHistoryId") == null) {
            resultMap.put("result", "fail");
            resultMap.put("message", "구매내역 저장 실패");
            return resultMap;
        }
        long purchaseHistoryId = ((Number) historyParam.get("purchaseHistoryId")).longValue();

        // 각 곡 insert (곡당 1음표)
        int saved = 0;
        for (PurchaseItem it : items) {
            if (it == null) continue;
            if (it.getMusicId() == null || it.getMusicId().isBlank()) continue;
            if (it.getAtThatCoin() <= 0) continue;

            Map<String, Object> musicParam = new HashMap<>();
            musicParam.put("purchaseHistoryId", purchaseHistoryId);
            musicParam.put("musicId", it.getMusicId());
            saved += purchaseDAO.insertPurchaseMusic(musicParam);
        }

        // 이번 건만 반영해서 코인 감소
        Map<String, Object> dec = new HashMap<>();
        dec.put("userId", userId);
        dec.put("delta", totalUsedCoin);
        purchaseDAO.decreaseUserCoin(dec);

        // 구매 후 잔액 = DB RETURNING 값 우선 사용
        long afterBalance;
        Number afterCoinOut = (Number) dec.get("afterCoin"); 
        if (afterCoinOut != null) afterBalance = afterCoinOut.longValue();
        else afterBalance = beforeBalance - totalUsedCoin; // fallback

        // 구매내역 행에 확정 잔액 반영
        Map<String, Object> upd = new HashMap<>();
        upd.put("purchaseHistoryId", purchaseHistoryId);
        upd.put("userId", userId);
        upd.put("atThatUserCoin", afterBalance); // DB가 준 값 우선
        purchaseDAO.updatePurchaseHistoryBalance(upd);

        resultMap.put("result", "success");
        resultMap.put("purchaseHistoryId", purchaseHistoryId);
        resultMap.put("savedCount", saved);
        resultMap.put("totalUsedCoin", totalUsedCoin);
        resultMap.put("coinBalance", afterBalance);

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
