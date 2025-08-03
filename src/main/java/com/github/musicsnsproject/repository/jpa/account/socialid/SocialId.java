package com.github.musicsnsproject.repository.jpa.account.socialid;

import com.github.musicsnsproject.common.myenum.OAuthProvider;
import com.github.musicsnsproject.repository.jpa.account.user.MyUser;
import jakarta.persistence.*;
import org.hibernate.annotations.DynamicInsert;

import java.time.LocalDateTime;

@Entity
@Table(name = "social_ids")
@DynamicInsert
public class SocialId {

    @EmbeddedId
    private SocialIdPk id;

    private LocalDateTime connectedAt;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private MyUser myUser;

    public static SocialId onlyId(String socialId, OAuthProvider provider) {
        SocialId socialIdEntity = new SocialId();
        socialIdEntity.id = SocialIdPk.of(socialId, provider);
        return socialIdEntity;
    }
}