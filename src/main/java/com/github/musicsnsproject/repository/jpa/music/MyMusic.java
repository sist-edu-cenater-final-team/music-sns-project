package com.github.musicsnsproject.repository.jpa.music;

import com.github.musicsnsproject.common.converter.custom.MyMusicTypeConverter;
import com.github.musicsnsproject.common.myenum.MyMusicType;
import com.github.musicsnsproject.repository.jpa.music.gift.GiftBox;
import com.github.musicsnsproject.repository.jpa.music.purchase.PurchaseHistory;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Check;
@Getter
@Entity
@Table(name = "my_music")
@Check(constraints = "source_type IN ('구매','선물')")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MyMusic {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long myMusicId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "purchase_history_id")
    private PurchaseHistory purchaseHistory;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gift_box_id")
    private GiftBox giftBox;

    @Convert(converter = MyMusicTypeConverter.class)
    private MyMusicType sourceType;
}
