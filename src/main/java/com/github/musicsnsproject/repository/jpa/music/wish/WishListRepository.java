package com.github.musicsnsproject.repository.jpa.music.wish;

import org.springframework.data.jpa.repository.JpaRepository;

public interface WishListRepository extends JpaRepository<WishList, Long>, WishListQueryRepository {
}
