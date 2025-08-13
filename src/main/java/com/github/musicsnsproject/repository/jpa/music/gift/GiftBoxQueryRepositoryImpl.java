package com.github.musicsnsproject.repository.jpa.music.gift;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class GiftBoxQueryRepositoryImpl implements GiftBoxQueryRepository{
    private final JPAQueryFactory queryFactory;
}
