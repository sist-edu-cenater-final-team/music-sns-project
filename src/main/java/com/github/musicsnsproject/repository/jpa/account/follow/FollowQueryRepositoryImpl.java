package com.github.musicsnsproject.repository.jpa.account.follow;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class FollowQueryRepositoryImpl implements FollowQueryRepository {
    private final JPAQueryFactory queryFactory;
}
