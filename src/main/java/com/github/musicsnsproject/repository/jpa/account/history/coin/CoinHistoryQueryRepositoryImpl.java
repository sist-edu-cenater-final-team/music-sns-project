package com.github.musicsnsproject.repository.jpa.account.history.coin;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CoinHistoryQueryRepositoryImpl implements CoinHistoryQueryRepository {

    private final JPAQueryFactory queryFactory;
}
