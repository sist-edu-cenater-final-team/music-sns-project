package com.github.musicsnsproject.repository.jpa.music.cart;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MusicCartRepository extends JpaRepository<MusicCart, Long>, MusicCartQueryRepository {
}
