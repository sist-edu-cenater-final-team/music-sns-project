package com.github.musicsnsproject.repository.jpa.community.block;

import com.github.musicsnsproject.repository.jpa.account.user.MyUser;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;

import java.time.LocalDateTime;

@Entity
@Table(name = "block_users")
@Getter
public class BlockUser {
    @EmbeddedId
    private BlockUserPk blockUserPk;

    @Column(name = "blocked_at")
    private LocalDateTime blockedAt;

    public static BlockUser onlyId(BlockUserPk blockUserPk) {
        BlockUser favoriteUser = new BlockUser();
        favoriteUser.blockUserPk = blockUserPk;
        return favoriteUser;
    }
    // user 엔티티에서 매핑을위해 커스텀 게터 메서드 생성
    public MyUser getMyUser() {
        return blockUserPk.getMyUser();
    }

    public MyUser getBlockUser() {
        return blockUserPk.getBlockUser();
    }

}