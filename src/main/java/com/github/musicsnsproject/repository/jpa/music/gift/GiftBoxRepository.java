package com.github.musicsnsproject.repository.jpa.music.gift;

import org.springframework.data.jpa.repository.JpaRepository;

public interface GiftBoxRepository extends JpaRepository<GiftBox, Long>, GiftBoxQueryRepository {
}
