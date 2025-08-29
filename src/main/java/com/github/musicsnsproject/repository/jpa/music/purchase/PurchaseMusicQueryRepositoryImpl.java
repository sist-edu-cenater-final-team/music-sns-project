package com.github.musicsnsproject.repository.jpa.music.purchase;

import com.github.musicsnsproject.common.exceptions.CustomNotAcceptException;
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


    // userId로 본인 PurchaseMusic 정보 조회하기
    @Override
    public List<PurchaseMusic> findByPurchaseMusicUserId(Long userId) {
        if (userId == null) {
            throw CustomNotAcceptException.of()
                    .customMessage("사용자를 찾을 수 없습니다.")
                    .request(userId)
                    .build();
        }

        return queryFactory.selectFrom(purchaseMusic)
                .join(purchaseMusic.purchaseHistory, purchaseHistory).fetchJoin()
                .join(purchaseHistory.myUser, myUser).fetchJoin()
                .where(purchaseHistory.myUser.userId.eq(userId))
                .orderBy(purchaseHistory.purchasedAt.desc())
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

        // 1) content 조회 (현재 페이지 데이터)
        List<PurchaseMusic> content = queryFactory
                .selectFrom(purchaseMusic)
                .join(purchaseMusic.purchaseHistory, purchaseHistory).fetchJoin()
                .join(purchaseHistory.myUser, myUser).fetchJoin()
                .where(purchaseHistory.myUser.userId.eq(userId))
                .orderBy(purchaseHistory.purchasedAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // 2) count 조회 (전체 개수)
        Long total = queryFactory
                .select(purchaseMusic.count())
                .from(purchaseMusic)
                .join(purchaseMusic.purchaseHistory, purchaseHistory)
                .join(purchaseHistory.myUser, myUser)
                .where(purchaseHistory.myUser.userId.eq(userId))
                .fetchOne();

        // 3) Page 객체 생성
        return new PageImpl<>(content, pageable, total == null ? 0 : total);
    }
}
