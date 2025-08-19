package com.github.musicsnsproject.repository.jpa.community.post;

import com.github.musicsnsproject.repository.jpa.account.user.MyUser;
import com.github.musicsnsproject.repository.jpa.emotion.UserEmotion;
import com.github.musicsnsproject.web.dto.post.WriteRequest;
import jakarta.persistence.*;
import lombok.Getter;
import org.hibernate.annotations.DynamicInsert;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "posts")
@Getter
@DynamicInsert
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
    private String title;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "post")
    private List<PostImage> images;

    public static Post onlyId(long postId) {
        Post post = new Post();
        post.postId = postId;
        return post;
    }

    public static Post of(WriteRequest request, UserEmotion userEmotion){
        Post post = new Post();
        post.title = request.getTitle();
        post.contents = request.getContents();
        post.userEmotion = userEmotion;


        //postImage 비슷하게 생성해서 넣어주기
        return post;
    }
}