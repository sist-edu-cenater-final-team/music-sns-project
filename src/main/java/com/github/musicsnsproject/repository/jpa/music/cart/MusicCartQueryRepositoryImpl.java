package com.github.musicsnsproject.repository.jpa.music.cart;

import com.github.musicsnsproject.common.exceptions.CustomNotAcceptException;
import com.github.musicsnsproject.repository.jpa.account.user.MyUser;
import com.github.musicsnsproject.repository.jpa.music.MyMusic;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static com.github.musicsnsproject.repository.jpa.account.user.QMyUser.myUser;
import static com.github.musicsnsproject.repository.jpa.music.cart.QMusicCart.musicCart;
import static com.github.musicsnsproject.repository.jpa.music.purchase.QPurchaseMusic.purchaseMusic;

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
                .where(musicCart.myUser.userId.eq(userId))
                .fetch();
    }

    @Override
    public boolean cartTrackCheck(MyUser user, String trackId) {
        return queryFactory
                .selectOne()
                .from(musicCart)
                .where(musicCart.myUser.eq(user)
                        .and(musicCart.musicId.eq(trackId)))
                .fetchFirst() != null;
    }

    @Override
    public boolean purchasedTrackCheck(MyUser user, String trackId) {
        return queryFactory
                .selectOne()
                .from(purchaseMusic)
                .join(musicCart).on(
                        musicCart.musicId.eq(purchaseMusic.musicId)
                )
                .where(
                    musicCart.myUser.eq(user)
                            .and(purchaseMusic.musicId.eq(trackId))
                )
                .fetchOne() != null;
    }
}
