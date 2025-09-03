package com.github.musicsnsproject.repository.jpa.music.profile;

import java.util.List;

import com.github.musicsnsproject.common.exceptions.CustomNotAcceptException;
import com.github.musicsnsproject.repository.jpa.music.purchase.PurchaseMusic;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import com.github.musicsnsproject.domain.ProfileMusicVO;
import com.github.musicsnsproject.repository.jpa.emotion.QEmotion;
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
    				profileMusic.count().as("count")
    			))
    			.from(profileMusic)
    			.leftJoin(myMusic)
    				.on(profileMusic.myMusic.myMusicId.eq(myMusic.myMusicId))
    			.leftJoin(history)
    				.on(myMusic.purchaseHistory.purchaseHistoryId.eq(history.purchaseHistoryId))
    			.leftJoin(music)
    				.on(history.purchaseHistoryId.eq(music.purchaseHistory.purchaseHistoryId))
    			.where(profileMusic.userEmotion.userEmotionId.eq(emotionId))
    	        .groupBy(music.musicId)
    	        .orderBy(profileMusic.count().desc())
    			.limit(5)
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

    // myMusicId 찾기
    @Override
    public Long findMyMusicId(Long userId, String musicId) {
        return queryFactory.select(myMusic.myMusicId)
                .from(myMusic)
                .join(myMusic.purchaseHistory, purchaseHistory)
                .join(purchaseMusic).on(purchaseMusic.purchaseHistory.eq(purchaseHistory))
                .join(purchaseHistory.myUser, myUser)
                .where(
                        myUser.userId.eq(userId)
                                .and(purchaseMusic.musicId.eq(musicId))
                )
                .orderBy(myMusic.myMusicId.desc())
                .fetchOne();
    }

    // myMusicId 중복체크
    @Override
    public boolean duplicateCheck(Long userId, String musicId, Long myMusicId) {

        return queryFactory.selectOne()
                .from(profileMusic)
                .join(profileMusic.myMusic, myMusic)
                .join(myMusic.purchaseHistory, purchaseHistory)
                .join(purchaseMusic).on(
                        purchaseMusic.purchaseHistory.eq(purchaseHistory)
                                .and(purchaseMusic.musicId.eq(musicId))
                )
                .join(purchaseHistory.myUser, myUser)
                .where(
                        myUser.userId.eq(userId)
                                .and(myMusic.myMusicId.eq(myMusicId))
                )
                .fetchFirst() != null;


    }


    // 프로필 설정 등록된 musicId 구하기
    @Override
    public List<String> getAddMusicId(Long userId, String musicId) {

        return queryFactory
                .select(purchaseMusic.musicId)
                .from(profileMusic)
                .join(profileMusic.myMusic, myMusic)
                .join(myMusic.purchaseHistory, purchaseHistory)
                .join(purchaseHistory.myUser, myUser)
                .join(purchaseMusic).on(
                        purchaseMusic.purchaseHistory.eq(purchaseHistory)
                                .and(purchaseMusic.musicId.eq(musicId))
                )
                .where(myUser.userId.eq(userId))
                .groupBy(purchaseMusic.musicId)
                .limit(1)
                .fetch();




    }


}
