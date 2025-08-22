package com.github.musicsnsproject.web.dto.music.purchase;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
// 구매내역 응답용
public class PurchaseResponse {
    private String musicName;
    private String albumName;
    private String albumImageUrl;
    private String artistName;
    private long atThatCoin;

    private String sourceType;
}
