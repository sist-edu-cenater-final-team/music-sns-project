package com.github.musicsnsproject.repository.jpa.music.profile;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ProfileMusicRepository extends JpaRepository<ProfileMusic, Long>, ProfileMusicQueryRepository {
}
