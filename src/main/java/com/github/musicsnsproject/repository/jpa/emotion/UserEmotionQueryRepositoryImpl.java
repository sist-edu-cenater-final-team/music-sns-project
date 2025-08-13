package com.github.musicsnsproject.repository.jpa.emotion;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class UserEmotionQueryRepositoryImpl implements UserEmotionQueryRepository {
    private final JPAQueryFactory queryFactory;
}
