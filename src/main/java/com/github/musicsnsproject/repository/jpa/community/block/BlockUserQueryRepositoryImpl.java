package com.github.musicsnsproject.repository.jpa.community.block;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class BlockUserQueryRepositoryImpl implements BlockUserQueryRepository{
    private final JPAQueryFactory queryFactory;
}
