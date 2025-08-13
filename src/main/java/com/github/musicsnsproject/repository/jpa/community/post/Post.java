package com.github.musicsnsproject.repository.jpa.community.post;

import com.github.musicsnsproject.repository.jpa.account.user.MyUser;
import com.github.musicsnsproject.repository.jpa.emotion.UserEmotion;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;
<<<<<<< HEAD

@Entity
@Table(name = "posts")
@Getter
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long postId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_emotion_id", nullable = false)
    private UserEmotion userEmotion;

    private long sharedCount;

    private String contents;

    private long viewCount;

    private LocalDateTime createdAt;
=======
import java.util.List;

@Entity
@Table(name = "posts")
@Getter
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long postId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_emotion_id", nullable = false)
    private UserEmotion userEmotion;

    private long sharedCount;

    private String contents;

    private long viewCount;

    private LocalDateTime createdAt;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "post")
    private List<PostImage> images;
>>>>>>> branch 'main' of https://github.com/sist-edu-cenater-final-team/music-sns-project.git

    public static Post onlyId(long postId) {
        Post post = new Post();
        post.postId = postId;
        return post;
    }
}