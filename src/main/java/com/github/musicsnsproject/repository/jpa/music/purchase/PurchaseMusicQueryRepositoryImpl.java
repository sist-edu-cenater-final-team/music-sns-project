package com.github.musicsnsproject.repository.jpa.music.purchase;

import com.github.musicsnsproject.common.exceptions.CustomNotAcceptException;
import com.github.musicsnsproject.repository.jpa.account.user.QMyUser;
import com.github.musicsnsproject.repository.jpa.music.QMyMusic;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;


import static com.github.musicsnsproject.repository.jpa.account.user.QMyUser.myUser;
import static com.github.musicsnsproject.repository.jpa.music.purchase.QPurchaseMusic.purchaseMusic;
import static com.github.musicsnsproject.repository.jpa.music.purchase.QPurchaseHistory.purchaseHistory;

@RequiredArgsConstructor
public class PurchaseMusicQueryRepositoryImpl implements PurchaseMusicQueryRepository{
    private final JPAQueryFactory queryFactory;

    @Override
    public Long findOneMyMusicIdByMusicId(Long userId, String musicId) {
        QMyMusic myMusic = QMyMusic.myMusic;
        QPurchaseMusic purchaseMusic = QPurchaseMusic.purchaseMusic;
        QPurchaseHistory purchaseHistory = QPurchaseHistory.purchaseHistory;

        return queryFactory
                .select(myMusic.myMusicId)
                .from(myMusic)
                .join(myMusic.purchaseHistory, purchaseHistory)
                .join(purchaseMusic).on(
                        purchaseMusic.purchaseHistory.eq(purchaseHistory)
                                .and(purchaseMusic.musicId.eq(musicId))
                )
                .where(purchaseHistory.myUser.userId.eq(userId))
                .fetchFirst();
    }


    // 구매헀던 음악 찾기
    @Override
    public List<String> findPurchasedMusicIds(Long userId, List<String> cartMusicIds) {

        return queryFactory
                .select(purchaseMusic.musicId)
                .from(purchaseMusic)
                .join(purchaseMusic.purchaseHistory, purchaseHistory)
                .join(myUser).on(purchaseHistory.myUser.eq(myUser))
                .where(
                        myUser.userId.eq(userId)
                                .and(purchaseMusic.musicId.in(cartMusicIds))
                )
                .fetch();
    }

    @Override
    public Page<PurchaseMusic> findPageByPurchaseMusicUserId(Long userId, Pageable pageable) {
        if (userId == null) {
            throw CustomNotAcceptException.of()
                    .customMessage("사용자를 찾을 수 없습니다.")
                    .request(userId)
                    .build();
        }

        // content 조회
        List<PurchaseMusic> content = queryFactory
                .selectFrom(purchaseMusic)
                .join(purchaseMusic.purchaseHistory, purchaseHistory).fetchJoin()
                .join(purchaseHistory.myUser, myUser).fetchJoin()
                .where(purchaseHistory.myUser.userId.eq(userId))
                .orderBy(purchaseHistory.purchasedAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // 전체 개수 조회
        Long total = queryFactory
                .select(purchaseMusic.count())
                .from(purchaseMusic)
                .join(purchaseMusic.purchaseHistory, purchaseHistory)
                .join(purchaseHistory.myUser, myUser)
                .where(purchaseHistory.myUser.userId.eq(userId))
                .fetchOne();

        // Page 객체 생성
        return new PageImpl<>(content, pageable, total == null ? 0 : total);
    }
}
