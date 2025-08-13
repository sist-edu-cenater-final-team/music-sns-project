package com.github.musicsnsproject.repository.jpa.music.wish;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class WishListQueryRepositoryImpl implements  WishListQueryRepository {
    private final JPAQueryFactory queryFactory;
}
