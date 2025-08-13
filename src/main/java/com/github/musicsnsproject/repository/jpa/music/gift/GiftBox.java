package com.github.musicsnsproject.repository.jpa.music.gift;

import com.github.musicsnsproject.common.converter.custom.GiftStatusConverter;
import com.github.musicsnsproject.common.converter.custom.MyMusicTypeConverter;
import com.github.musicsnsproject.common.myenum.GiftStatus;
import com.github.musicsnsproject.repository.jpa.account.user.MyUser;
import com.github.musicsnsproject.repository.jpa.music.purchase.PurchaseHistory;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;

@Entity
@Table(name = "gift_box")
@Getter
public class GiftBox {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long giftBoxId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "purchase_history_id", nullable = false)
    private PurchaseHistory purchaseHistory;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id", nullable = false)
    private MyUser receiver;
    @Convert(converter = GiftStatusConverter.class)
    @Column(name = "status")
    private GiftStatus giftStatus;


    private LocalDateTime createdAt;
}