package com.github.musicsnsproject.repository.jpa.music.profile;

import java.util.List;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import com.github.musicsnsproject.domain.ProfileMusicVO;
import com.github.musicsnsproject.repository.jpa.music.QMyMusic;
import com.github.musicsnsproject.repository.jpa.music.purchase.QPurchaseHistory;
import com.github.musicsnsproject.repository.jpa.music.purchase.QPurchaseMusic;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import static com.github.musicsnsproject.repository.jpa.account.user.QMyUser.myUser;
import static com.github.musicsnsproject.repository.jpa.music.QMyMusic.myMusic;
import static com.github.musicsnsproject.repository.jpa.music.profile.QProfileMusic.profileMusic;
import static com.github.musicsnsproject.repository.jpa.music.purchase.QPurchaseHistory.purchaseHistory;
import static com.github.musicsnsproject.repository.jpa.music.purchase.QPurchaseMusic.purchaseMusic;

@RequiredArgsConstructor
@Primary
@Repository
public class ProfileMusicQueryRepositoryImpl implements ProfileMusicQueryRepository{
    private final JPAQueryFactory queryFactory;

    @Override
    public List<ProfileMusicVO> emotionPlayList(Long emotionId) {
    	QProfileMusic profileMusic = QProfileMusic.profileMusic;
    	QPurchaseMusic music = QPurchaseMusic.purchaseMusic;
    	QMyMusic myMusic = QMyMusic.myMusic;
    	QPurchaseHistory history = QPurchaseHistory.purchaseHistory;
    	
    	return queryFactory.select(Projections.fields(ProfileMusicVO.class, 
    				music.musicId.as("musicId"),
    				profileMusic.myMusic.myMusicId.count().as("count")
    			))
    			.from(profileMusic)
    			.leftJoin(myMusic)
    				.on(profileMusic.myMusic.myMusicId.eq(myMusic.myMusicId))
    			.leftJoin(history)
    				.on(myMusic.purchaseHistory.purchaseHistoryId.eq(history.purchaseHistoryId))
    			.leftJoin(music)
    				.on(history.purchaseHistoryId.eq(music.purchaseHistory.purchaseHistoryId))
    			.where(profileMusic.userEmotion.emotion.emotionId.eq(emotionId))
    	        .groupBy(music.musicId)
    	        .orderBy(profileMusic.count().desc())
    			.limit(10)
    			.fetch();
    		
    }

	@Override
	public List<ProfileMusicVO> profileMusicId(Long userId) {
		
    	QProfileMusic profileMusic = QProfileMusic.profileMusic;
    	QPurchaseMusic music = QPurchaseMusic.purchaseMusic;
    	QMyMusic myMusic = QMyMusic.myMusic;
    	QPurchaseHistory history = QPurchaseHistory.purchaseHistory;
		
    	return queryFactory
    		    .select(Projections.fields(ProfileMusicVO.class,
    		        music.musicId.as("musicId")
    		    ))
    		    .from(profileMusic)
    		    .innerJoin(myMusic)
    		        .on(profileMusic.myMusic.myMusicId.eq(myMusic.myMusicId))
    		    .innerJoin(history)
    		        .on(myMusic.purchaseHistory.purchaseHistoryId.eq(history.purchaseHistoryId)
    		            .and(history.myUser.userId.eq(userId)))
    		    .innerJoin(music)
    		        .on(history.purchaseHistoryId.eq(music.purchaseHistory.purchaseHistoryId))
    		    .fetch();
				
				
	}

    @Override
    public boolean duplicateCheck(Long userId, String musicId) {
        return queryFactory
                .selectOne()
                .from(profileMusic)
                .join(profileMusic.myMusic, myMusic)
                .join(myMusic.purchaseHistory, purchaseHistory)
                .join(purchaseMusic).on(purchaseMusic.purchaseHistory.eq(purchaseHistory))
                .where(
                        purchaseHistory.myUser.userId.eq(userId),
                        profileMusic.musicId.eq(musicId)
                )
                .fetchFirst() != null;
    }

    @Override
    public List<Long> findMyMusicIdsByUserId(Long userId) {
        QProfileMusic profileMusic = QProfileMusic.profileMusic;

        return queryFactory
                .select(profileMusic.myMusic.myMusicId)
                .from(profileMusic)
                .where(profileMusic.userEmotion.myUser.userId.eq(userId))
                .orderBy(profileMusic.listOrder.asc())
                .fetch();
    }

    @Override
    public List<ProfileMusic> findMyMusics(Long userId) {
        return queryFactory
                .selectFrom(profileMusic)
                .join(myMusic.purchaseHistory, purchaseHistory)
                .join(purchaseHistory.myUser, myUser)
                .join(purchaseMusic).on(purchaseMusic.purchaseHistory.eq(purchaseHistory))
                .where(
                        myUser.userId.eq(userId)
                )
                .orderBy(profileMusic.listOrder.asc())
                .fetch();
    }

    // 삭제할 프로필 음악 찾기
    @Override
    public ProfileMusic findDeleteByMusicId(Long userId, String musicId) {

        return queryFactory
                .selectFrom(profileMusic)
                .join(profileMusic.myMusic, myMusic)
                .join(myMusic.purchaseHistory, purchaseHistory)
                .join(purchaseHistory.myUser, myUser)
                .where(
                        myUser.userId.eq(userId)
                                .and(profileMusic.musicId.eq(musicId))
                )
                .fetchOne();
    }


    // 삭제할 대상 찾기
    @Override
    public List<ProfileMusic> findAllAfterDelete(Long userId, int deletedOrder) {
        return queryFactory
                .selectFrom(profileMusic)
                .join(profileMusic.myMusic, myMusic)
                .join(myMusic.purchaseHistory, purchaseHistory)
                .join(purchaseHistory.myUser, myUser)
                .where(myUser.userId.eq(userId)
                        .and(profileMusic.listOrder.gt(deletedOrder)))
                .fetch();
    }
}
