package com.github.musicsnsproject.repository.jpa.community.post;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PostImageRepository extends JpaRepository<PostImage, Long> {

    @Modifying(clearAutomatically = true)
    @Query("delete from PostImage p where p.post.postId = :postId")
    void deleteByPostId(@Param("postId") Long postId);


    @Query("select p.postImageUrl from PostImage p where p.post.postId = :postId")
    List<String> findPostImageUrlsByPostId(@Param("postId") Long postId);
}
