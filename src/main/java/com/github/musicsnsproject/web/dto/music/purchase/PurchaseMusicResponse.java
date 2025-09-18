package com.github.musicsnsproject.web.dto.music.purchase;

import com.github.musicsnsproject.domain.purchase.PurchaseMusicVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
// 구매한 음악 응답용
public class PurchaseMusicResponse {

    private List<PurchaseMusicVO> purchaseMusic;
    private int pageNo;
    private int pageSize;
    private long totalElements;
    private int totalPages;
    private boolean pageLast;
}
