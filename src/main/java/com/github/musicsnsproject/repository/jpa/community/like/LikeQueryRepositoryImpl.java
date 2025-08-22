package com.github.musicsnsproject.repository.jpa.community.like;

import com.querydsl.core.types.dsl.Wildcard;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class LikeQueryRepositoryImpl implements LikeQueryRepository{
    private final JPAQueryFactory queryFactory;

    @Override
    public Long countLikeBy(Long postId) {

        Long n = queryFactory.select(Wildcard.count)
                .from(QLike.like)
                .where(QLike.like.likePk.post.postId.eq(postId))
                .fetchOne();
        return n;
    }
}
