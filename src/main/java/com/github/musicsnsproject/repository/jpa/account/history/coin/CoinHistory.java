package com.github.musicsnsproject.repository.jpa.account.history.coin;

import com.github.musicsnsproject.repository.jpa.account.user.MyUser;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;

@Table(name = "coin_history")
@Entity
@Getter
public class CoinHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long coinHistoryId;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private MyUser myUser;

    @Column(nullable = false)
    private long coin;

    private long atThatPrice;

    @Column(nullable = false)
    private LocalDateTime createdAt;
}
