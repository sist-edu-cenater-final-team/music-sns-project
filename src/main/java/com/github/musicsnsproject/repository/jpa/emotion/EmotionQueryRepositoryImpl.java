package com.github.musicsnsproject.repository.jpa.emotion;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class EmotionQueryRepositoryImpl implements EmotionQueryRepository {
    private final JPAQueryFactory queryFactory;

}
