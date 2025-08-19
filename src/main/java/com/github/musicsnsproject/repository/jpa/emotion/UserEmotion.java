package com.github.musicsnsproject.repository.jpa.emotion;

import com.github.musicsnsproject.repository.jpa.account.user.MyUser;
import jakarta.persistence.*;
import lombok.Getter;
import org.hibernate.annotations.DynamicInsert;

import java.time.LocalDateTime;

@Table(name= "user_emotions")
@Entity
@Getter
@DynamicInsert
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

    public static UserEmotion fromUserEmotion(Emotion emotion, MyUser loginUser){
        UserEmotion userEmotion = new UserEmotion();
        userEmotion.emotion = emotion;
        userEmotion.myUser = loginUser;
        return userEmotion;
    }
}
