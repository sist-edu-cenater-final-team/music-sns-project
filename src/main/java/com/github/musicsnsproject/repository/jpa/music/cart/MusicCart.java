package com.github.musicsnsproject.repository.jpa.music.cart;

import com.github.musicsnsproject.repository.jpa.account.user.MyUser;
import jakarta.persistence.*;
<<<<<<< HEAD
import lombok.Getter;
=======
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
>>>>>>> branch 'main' of https://github.com/sist-edu-cenater-final-team/music-sns-project.git

import java.time.LocalDateTime;
@Getter
@Entity
@Table(name = "music_cart")
public class MusicCart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long musicCartId;

    private String musicId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private MyUser myUser;

    private LocalDateTime createdAt;
}