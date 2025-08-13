package com.github.musicsnsproject.repository.jpa.community.like;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class LikeQueryRepositoryImpl implements LikeQueryRepository{
    private final JPAQueryFactory queryFactory;
}
