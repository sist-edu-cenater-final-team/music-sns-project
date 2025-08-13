package com.github.musicsnsproject.repository.jpa.emotion;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserEmotionRepository extends JpaRepository<UserEmotion, Long>, UserEmotionQueryRepository {

    // Additional query methods can be defined here if needed
}
