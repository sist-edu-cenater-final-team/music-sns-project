package com.github.musicsnsproject.repository.jpa.music.purchase;

import com.github.musicsnsproject.common.exceptions.CustomNotAcceptException;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;


import static com.github.musicsnsproject.repository.jpa.account.user.QMyUser.myUser;
import static com.github.musicsnsproject.repository.jpa.music.cart.QMusicCart.musicCart;
import static com.github.musicsnsproject.repository.jpa.music.QMyMusic.myMusic;
import static com.github.musicsnsproject.repository.jpa.music.purchase.QPurchaseMusic.purchaseMusic;
import static com.github.musicsnsproject.repository.jpa.music.purchase.QPurchaseHistory.purchaseHistory;

@RequiredArgsConstructor
public class PurchaseMusicQueryRepositoryImpl implements PurchaseMusicQueryRepository{
    private final JPAQueryFactory queryFactory;


    // userId로 본인 PurchaseMusic 정보 조회하기
    @Override
    public List<PurchaseMusic> findByPurchaseMusicUserId(Long userId) {
        if (userId == null) {
            throw CustomNotAcceptException.of()
                    .customMessage("사용자를 찾을 수 없습니다.")
                    .request(userId)
                    .build();
        }

        /*
        select PM.PURCHASE_HISTORY_ID, U.USER_ID, U.NICKNAME, PM.MUSIC_ID
        from PURCHASE_MUSIC PM
        join PURCHASE_HISTORY PH ON PH.PURCHASE_HISTORY_ID = PM.PURCHASE_HISTORY_ID
        join users U ON U.USER_ID = PH.USER_ID
        where U.USER_ID = 4;

         */

        return queryFactory.selectFrom(purchaseMusic)
                .join(purchaseMusic.purchaseHistory, purchaseHistory).fetchJoin()
                .join(purchaseHistory.myUser, myUser).fetchJoin()
                .where(purchaseHistory.myUser.userId.eq(userId))
                .fetch();
    }
}
