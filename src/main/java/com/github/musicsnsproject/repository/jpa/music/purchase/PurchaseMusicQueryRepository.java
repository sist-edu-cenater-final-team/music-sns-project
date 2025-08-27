package com.github.musicsnsproject.repository.jpa.music.purchase;

import java.util.List;

public interface PurchaseMusicQueryRepository {

    // userId로 본인 PurchaseMusic 정보 조회하기
    List<PurchaseMusic> findByPurchaseMusicUserId(Long userId);
}
