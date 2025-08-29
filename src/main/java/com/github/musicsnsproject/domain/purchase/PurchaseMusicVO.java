package com.github.musicsnsproject.domain.purchase;


import com.github.musicsnsproject.repository.jpa.music.purchase.PurchaseMusic;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PurchaseMusicVO {
    private long purchaseMusicId;

    private String musicId;
    private String musicName;
    private String albumId;
    private String albumName;
    private String albumImageUrl;
    private String artistId;
    private String artistName;
    private long purchaseHistoryId;
}
