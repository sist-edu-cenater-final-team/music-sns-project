package com.github.musicsnsproject.repository.jpa.community.block;

import com.github.musicsnsproject.repository.jpa.account.user.MyUser;
import jakarta.persistence.Embeddable;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Embeddable
@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class BlockUserPk {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private MyUser myUser;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "block_user_id")
    private MyUser blockUser;

    @EqualsAndHashCode.Include
    public Long getMyUserId() {
        return myUser != null ? myUser.getUserId() : null;
    }

    @EqualsAndHashCode.Include
    public Long getBlockUserId() {
        return blockUser != null ? blockUser.getUserId() : null;
    }
    public static BlockUserPk onlyId(long userId, long blockUserUserId) {
        BlockUserPk blockUserPk = new BlockUserPk();
        blockUserPk.myUser = MyUser.onlyId(userId);
        blockUserPk.blockUser = MyUser.onlyId(blockUserUserId);
        return blockUserPk;
    }
}
