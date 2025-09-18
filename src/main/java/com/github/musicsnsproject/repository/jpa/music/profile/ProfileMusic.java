package com.github.musicsnsproject.repository.jpa.music.profile;

import com.github.musicsnsproject.repository.jpa.emotion.UserEmotion;
import com.github.musicsnsproject.repository.jpa.music.MyMusic;
import com.github.musicsnsproject.repository.jpa.music.purchase.PurchaseHistory;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "profile_musics")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProfileMusic {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long profileMusicId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "my_music_id", nullable = false)
    private MyMusic myMusic;

    private String musicId;

    private Integer listOrder;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_emotion_id", nullable = false)
    private UserEmotion userEmotion;

    private LocalDateTime createdAt;

    @PrePersist // INSERT 전에 호출한다.
    public void prePersist() {
        // createdAt가 null이라면 현재 시각을 넣어준다.
        // createdAt가 null이 아니라면 기존 시각을 보여준다.
        this.createdAt = this.createdAt == null ? LocalDateTime.now() : this.createdAt;
    }

    // 프로필 음악리스트 순번 업데이트용
    public void decrementListOrder() {
        this.listOrder--;
    }
}
