package com.github.musicsnsproject.repository.jpa.account.follow;

import com.github.musicsnsproject.repository.jpa.account.user.MyUser;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;

import java.time.LocalDateTime;

@Entity
@Table(name = "follows")
@Getter
public class Follow {

    @EmbeddedId
    private FollowPk followPk;

    @Column(name = "favorite")
    private boolean favorite;

    private LocalDateTime favoriteAt;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    public static Follow onlyId(FollowPk followPk) {
        Follow follow = new Follow();
        follow.followPk = followPk;
        return follow;
    }
    // user 엔티티에서 매핑을위해 커스텀 게터 메서드 생성
    public MyUser getFollowee() {
        return followPk.getFollowee();
    }

    public MyUser getFollower() {
        return followPk.getFollower();
    }


}
