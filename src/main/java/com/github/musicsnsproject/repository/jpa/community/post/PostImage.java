package com.github.musicsnsproject.repository.jpa.community.post;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Table(name="post_images")
@Getter
public class PostImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long postImageId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @Column(nullable = false)
    private String postImageUrl;

    @Column(nullable = false)
    private String postImageName;
}
