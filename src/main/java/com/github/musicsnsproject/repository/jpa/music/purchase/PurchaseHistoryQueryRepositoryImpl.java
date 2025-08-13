package com.github.musicsnsproject.repository.jpa.music.purchase;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PurchaseHistoryQueryRepositoryImpl implements PurchaseHistoryQueryRepository{
    private final JPAQueryFactory queryFactory;
}
