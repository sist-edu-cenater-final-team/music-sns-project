package com.github.musicsnsproject.repository.jpa.music.profile;

import java.util.List;

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

}
