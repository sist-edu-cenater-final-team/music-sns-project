package com.github.musicsnsproject.repository.jpa.community.like;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface LikeRepository extends JpaRepository<Like, LikePk>, LikeQueryRepository {

    @Query("select count(*) " +
            "from Like l " +
            "where l.likePk.post.postId = :postId "
    )
    Long countLikeCnt(@Param("postId") Long postId);

    @Modifying(clearAutomatically = true)
    @Query("delete from Like l where l.likePk.post.postId = :postId")
    void deleteByPostId(@Param("postId") Long postId);
}
