package com.github.musicsnsproject.repository.jpa.community.comment;

import com.github.musicsnsproject.repository.jpa.community.post.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;

public interface CommentRepository extends JpaRepository<Comment, Long>, CommentQueryRepository {

    void deleteByPostIn(Collection<Post> posts);

    @Modifying(clearAutomatically = true)
    @Query("delete from Comment c where c.post.postId = :postId")
    void deleteByPostId(@Param("postId") Long postId);
}
