package com.github.musicsnsproject.repository.jpa.account.follow;

import com.github.musicsnsproject.repository.jpa.account.user.MyUser;
import jakarta.persistence.Embeddable;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class FollowPk {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "followee_id")
    private MyUser followee;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "follower_id")
    private MyUser follower;

    @EqualsAndHashCode.Include
    public Long getFolloweeId() {
        return followee.getUserId();
    }
    @EqualsAndHashCode.Include
    public Long getFollowerId() {
        return follower.getUserId();
    }

    public static FollowPk onlyId(long followeeId, long followerId) {
        FollowPk followPk = new FollowPk();
        followPk.followee = MyUser.onlyId(followeeId);
        followPk.follower = MyUser.onlyId(followerId);
        return followPk;
    }

}
