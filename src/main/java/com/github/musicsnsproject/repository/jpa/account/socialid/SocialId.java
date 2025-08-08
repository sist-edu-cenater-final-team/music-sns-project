package com.github.musicsnsproject.repository.jpa.account.socialid;

import com.github.musicsnsproject.common.myenum.OAuthProvider;
import com.github.musicsnsproject.repository.jpa.account.user.MyUser;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;

import java.time.LocalDateTime;

@Entity
@Table(name = "social_ids")
@DynamicInsert
@Getter
@NoArgsConstructor
public class SocialId {

    @EmbeddedId
    private SocialIdPk socialIdPk;

    private LocalDateTime connectedAt;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private MyUser myUser;

    public static SocialId onlyId(String socialId, OAuthProvider provider) {
        SocialId socialIdEntity = new SocialId();
        socialIdEntity.socialIdPk = SocialIdPk.of(socialId, provider);
        return socialIdEntity;
    }

    public static SocialId ofSocialIdPkAndMyUser(SocialIdPk socialIdPk, MyUser myUser){
        SocialId socialId = new SocialId(socialIdPk.getSocialId(), socialIdPk.getProvider(), myUser);
        socialId.connectedAt = LocalDateTime.now();
        return socialId;
    }

    private SocialId(String socialId, OAuthProvider provider, MyUser myUser) {
        this.socialIdPk = SocialIdPk.of(socialId, provider);
        this.myUser = myUser;
    }
    public void socialConnectSetting(MyUser myUser){
        this.connectedAt = LocalDateTime.now();
        this.myUser = myUser;
    }
    public void socialConnectSetting(){
        this.connectedAt = LocalDateTime.now();
    }
}