package com.github.musicsnsproject.repository.jpa.music;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class MyMusicQueryRepositoryImpl implements MyMusicQueryRepository {
    private final JPAQueryFactory queryFactory;
}
