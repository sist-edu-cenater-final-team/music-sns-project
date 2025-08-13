package com.github.musicsnsproject.repository.jpa.emotion;

import com.github.musicsnsproject.repository.jpa.account.user.MyUser;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;

@Table(name= "user_emotions")
@Entity
@Getter
public class UserEmotion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userEmotionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private MyUser myUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "emotion_id", nullable = false)
    private Emotion emotion;

    private LocalDateTime createdAt;
}
