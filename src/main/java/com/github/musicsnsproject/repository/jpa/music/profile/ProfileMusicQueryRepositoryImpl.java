package com.github.musicsnsproject.repository.jpa.music.profile;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ProfileMusicQueryRepositoryImpl implements ProfileMusicQueryRepository{
    private final JPAQueryFactory queryFactory;
}
