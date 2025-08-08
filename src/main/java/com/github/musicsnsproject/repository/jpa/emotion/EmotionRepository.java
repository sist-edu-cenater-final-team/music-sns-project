package com.github.musicsnsproject.repository.jpa.emotion;

import org.springframework.data.jpa.repository.JpaRepository;

public interface EmotionRepository extends JpaRepository<Emotion, Long>, EmotionQueryRepository {
}
