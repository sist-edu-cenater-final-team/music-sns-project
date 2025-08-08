package com.github.musicsnsproject.repository.jpa.music;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MyMusicRepository extends JpaRepository<MyMusic, Long>, MyMusicQueryRepository {
}
