package com.github.musicsnsproject.repository.jpa.community.like;

import org.springframework.data.jpa.repository.JpaRepository;

public interface LikeRepository extends JpaRepository<Like, LikePk>, LikeQueryRepository {
}
