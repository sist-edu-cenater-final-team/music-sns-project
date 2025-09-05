package com.github.musicsnsproject.repository.jpa.community.post;

import com.github.musicsnsproject.repository.jpa.emotion.UserEmotion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PostRepository extends JpaRepository<Post, Long>, PostQueryRepository {

    @Modifying
    @Query("update Post p set p.title = :title, p.contents = :contents, p.userEmotion = :userEmotion, p.updatedAt = current_timestamp " +
            "where p.postId = :postId")
    int updatePostForEdit(@Param("postId") Long postId, @Param("title") String title, @Param("contents") String contents, @Param("userEmotion") UserEmotion userEmotion);
}
