package com.github.musicsnsproject.web.dto.music.purchase;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
// 구매한 음악 응답용
public class PurchaseMusicResponse {

    private String musicId;
    private String musicName;
    private String albumName;
    private String albumImageUrl;
    private String artistName;

    private long purchaseHistoryId;

    private long atThatCoin;
    private String sourceType;
}
