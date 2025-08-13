package com.github.musicsnsproject.repository.jpa.community.like;

import com.github.musicsnsproject.repository.jpa.account.user.MyUser;
import com.github.musicsnsproject.repository.jpa.community.post.Post;
import jakarta.persistence.Embeddable;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Embeddable
@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class LikePk {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private MyUser myUser;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    @EqualsAndHashCode.Include
    public Long getMyUserId() {
        return myUser != null ? myUser.getUserId() : null;
    }

    @EqualsAndHashCode.Include
    public Long getPostId() {
        return post != null ? post.getPostId() : null;
    }
    public static LikePk onlyId(long userId, long postId){
        LikePk likePk = new LikePk();
        likePk.myUser = MyUser.onlyId(userId);
        likePk.post = Post.onlyId(postId);
        return likePk;
    }
}
