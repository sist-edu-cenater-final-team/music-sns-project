package com.github.musicsnsproject.service.music.purchase;

import com.github.musicsnsproject.repository.jpa.music.purchase.PurchaseHistory;
import com.github.musicsnsproject.web.dto.music.purchase.PurchaseMusicResponse;

import java.util.List;

public interface PurchaseMusicService {
    List<PurchaseMusicResponse> getPurchaseMusicList(Long userId);
}
