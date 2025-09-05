package com.github.musicsnsproject.repository.jpa.music.purchase;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PurchaseMusicQueryRepository {

    Page<PurchaseMusic> findPageByPurchaseMusicUserId(Long userId, Pageable pageable);

    Long findOneMyMusicIdByMusicId(Long userId, String musicId);

    List<String> findPurchasedMusicIds(Long userId, List<String> cartMusicIds);
}
