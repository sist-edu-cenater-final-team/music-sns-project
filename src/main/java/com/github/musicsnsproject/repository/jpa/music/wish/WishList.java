package com.github.musicsnsproject.repository.jpa.music.wish;


import com.github.musicsnsproject.repository.jpa.account.user.MyUser;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;

@Entity
@Table(name = "wish_list")
@Getter
public class WishList {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long wishListId;

    private String musicId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private MyUser myUser;


    private LocalDateTime createdAt;
}
