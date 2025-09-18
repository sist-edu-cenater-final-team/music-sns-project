package com.github.musicsnsproject.service.music.purchase;

import com.github.musicsnsproject.web.dto.music.purchase.PurchaseMusicResponse;

public interface PurchaseMusicService {

    PurchaseMusicResponse getPurchaseMusicList(Long userId, int currentShowPageNo);

}
