package com.github.musicsnsproject.repository.jpa.community.like;


import com.github.musicsnsproject.repository.jpa.account.user.MyUser;
import com.github.musicsnsproject.repository.jpa.community.post.Post;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import org.hibernate.annotations.DynamicInsert;

import java.time.LocalDateTime;

@Entity
@Table(name = "likes")
@Getter
@DynamicInsert
public class Like {
    @EmbeddedId
    private LikePk likePk;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
    public static Like onlyId(LikePk likePk) {
        Like like = new Like();
        like.likePk = likePk;
        return like;
    }
    public MyUser getMyUser() {
        return this.likePk.getMyUser();
    }
    public Post getPost(){
        return this.likePk.getPost();
    }
}
