package com.github.musicsnsproject.repository.jpa.music.purchase;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Table(name = "purchase_music")
@Getter
public class PurchaseMusic {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long purchaseMusicId;

    private String musicId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name= "purchase_history_id")
    private PurchaseHistory purchaseHistory;
    private long atThatCoin;
}
