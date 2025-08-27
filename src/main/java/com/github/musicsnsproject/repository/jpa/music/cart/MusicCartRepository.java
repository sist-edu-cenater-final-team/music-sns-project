package com.github.musicsnsproject.repository.jpa.music.cart;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MusicCartRepository extends JpaRepository<MusicCart, Long>, MusicCartQueryRepository {


}
