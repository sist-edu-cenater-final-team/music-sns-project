package com.github.musicsnsproject.repository.jpa.account.history.login;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

import static com.github.musicsnsproject.repository.jpa.account.history.login.QLoginHistory.loginHistory;


@RequiredArgsConstructor
public class LoginHistoryQueryRepositoryImpl implements LoginHistoryQueryRepository {
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public LocalDateTime findLatestLoggedAtByUserId(Long userId) {
        return jpaQueryFactory.select(loginHistory.loggedAt)
                .from(loginHistory)
                .where(loginHistory.myUser.userId.eq(userId))
                .orderBy(loginHistory.loggedAt.desc())
                .fetchFirst();
    }
}
