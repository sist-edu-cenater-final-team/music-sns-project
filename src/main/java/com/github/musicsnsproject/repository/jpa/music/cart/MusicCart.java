package com.github.musicsnsproject.repository.jpa.music.cart;

import com.github.musicsnsproject.repository.jpa.account.user.MyUser;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;

import java.time.LocalDateTime;
@Getter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
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

    @PrePersist // INSERT 전에 호출한다.
    public void prePersist() {
        // createdAt가 null이라면 현재 시각을 넣어준다.
        // createdAt가 null이 아니라면 기존 시각을 보여준다.
        this.createdAt = this.createdAt == null ? LocalDateTime.now() : this.createdAt;
    }

    public CartResponse toDTO(){
        return CartResponse.builder()
                .cartId(this.musicCartId)
                .musicId(this.musicId)
                .userId(this.myUser.getUserId())
                .build();
    }
}