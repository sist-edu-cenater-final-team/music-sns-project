package com.github.musicsnsproject.repository.jpa.community.post;

import com.github.musicsnsproject.web.dto.post.FollowPostVO;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class PostQueryRepositoryImpl implements PostQueryRepository {
    private final JPAQueryFactory queryFactory;

    @Override
    public List<FollowPostVO> findFollowPostByuserId(Long userId) {



        return List.of();
    }
}
