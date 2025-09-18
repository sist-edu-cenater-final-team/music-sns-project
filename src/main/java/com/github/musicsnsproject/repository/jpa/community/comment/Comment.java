package com.github.musicsnsproject.repository.jpa.community.comment;

import com.github.musicsnsproject.repository.jpa.account.user.MyUser;
import com.github.musicsnsproject.repository.jpa.community.post.Post;
import jakarta.persistence.*;
import lombok.Getter;
import org.hibernate.annotations.DynamicInsert;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "comments")
@Getter
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long commentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private MyUser myUser;

    private String contents;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_comment_id")
    private Comment parentComment;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "root_comment_id")
    private Comment rootComment;

    @OneToMany(mappedBy = "parentComment", fetch = FetchType.LAZY)
    private List<Comment> childComments;
    @OneToMany(mappedBy = "rootComment", fetch = FetchType.LAZY)
    private List<Comment> allChildComments;

    @Column(name="created_at")
    private LocalDateTime createdAt;

    public static Comment onlyId(long commentId) {
        Comment comment = new Comment();
        comment.commentId = commentId;
        return comment;
    }


    public static Comment of(Post post, MyUser myUser, String comment, Comment parent) {
        Comment commentEntity = new Comment();
        commentEntity.post = post;
        commentEntity.myUser = myUser;
        commentEntity.contents = comment;
        commentEntity.parentComment = parent;
        commentEntity.createdAt = LocalDateTime.now();

        if (parent == null) {
            commentEntity.rootComment = commentEntity;
        }
        else {
            commentEntity.rootComment = (parent.getRootComment() != null) ? parent.rootComment : parent;
        }

        return commentEntity;
    }


}

