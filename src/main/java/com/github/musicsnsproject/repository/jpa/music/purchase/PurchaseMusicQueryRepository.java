package com.github.musicsnsproject.repository.jpa.music.purchase;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PurchaseMusicQueryRepository {

    // userId로 본인 PurchaseMusic 정보 조회하기
    List<PurchaseMusic> findByPurchaseMusicUserId(Long userId);

    Page<PurchaseMusic> findPageByPurchaseMusicUserId(Long userId, Pageable pageable);
}
