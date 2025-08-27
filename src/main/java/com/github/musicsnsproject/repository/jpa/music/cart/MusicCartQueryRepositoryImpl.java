package com.github.musicsnsproject.repository.jpa.music.cart;

import com.github.musicsnsproject.common.exceptions.CustomNotAcceptException;
import com.github.musicsnsproject.repository.jpa.account.user.MyUser;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static com.github.musicsnsproject.repository.jpa.account.user.QMyUser.myUser;
import static com.github.musicsnsproject.repository.jpa.music.cart.QMusicCart.musicCart;

@RequiredArgsConstructor
public class MusicCartQueryRepositoryImpl implements MusicCartQueryRepository {
    private final JPAQueryFactory queryFactory;


    @Override
    public MyUser findMyUser(Long userId) {
        if (userId == null) {
            throw CustomNotAcceptException.of()
                    .customMessage("사용자를 찾을 수 없습니다.")
                    .request(userId)
                    .build();
        }
        // 사용자가 있으면
        return queryFactory.selectFrom(myUser)
                .where(myUser.userId.eq(userId))
                .fetchOne();
    }

    // userId로 본인 musicCart 정보 조회하기
    @Override
    public List<MusicCart> findByCartUserId(Long userId) {

        if (userId == null) {
            throw CustomNotAcceptException.of()
                    .customMessage("사용자를 찾을 수 없습니다.")
                    .request(userId)
                    .build();
        }

        return queryFactory
                .selectFrom(musicCart)
                .join(musicCart.myUser, myUser).fetchJoin()
                .where(musicCart.myUser.userId.eq(userId))
                .orderBy(musicCart.createdAt.desc())
                .fetch();
    }
}
