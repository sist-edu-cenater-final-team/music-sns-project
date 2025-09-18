package com.github.musicsnsproject.service.mypage.eumpyo;

import java.util.List;
import java.util.Map;

public interface EumpyoPurchaseService {

    // 노래 1개 구매 기록
    Map<String, Object> purchase(long userId, String musicId, int atThatCoin);

    // 여러 노래 한꺼번에 구매 기록
    Map<String, Object> purchaseBulk(long userId, List<PurchaseItem> items);

    // 사용자 음표 재정산 (충전/구매 내역 기준)
    int recalcUserCoin(long userId);

    // 구매 품목(음악ID + 가격)
    final class PurchaseItem {
        private final String musicId;
        private final int atThatCoin;

        public PurchaseItem(String musicId, int atThatCoin) {
            this.musicId = musicId;
            this.atThatCoin = atThatCoin;
        }

        public String getMusicId() { return musicId; }
        public int getAtThatCoin() { return atThatCoin; }
    }
}
