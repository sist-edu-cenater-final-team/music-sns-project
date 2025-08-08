package com.github.musicsnsproject.repository.jpa.music.cart;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class MusicCartQueryRepositoryImpl implements MusicCartQueryRepository {
    private final JPAQueryFactory queryFactory;
}
