package com.github.musicsnsproject.service.music.profile;

import com.github.musicsnsproject.common.exceptions.CustomNotAcceptException;
import com.github.musicsnsproject.repository.jpa.emotion.UserEmotion;
import com.github.musicsnsproject.repository.jpa.emotion.UserEmotionRepository;
import com.github.musicsnsproject.repository.jpa.music.MyMusic;
import com.github.musicsnsproject.repository.jpa.music.MyMusicRepository;
import com.github.musicsnsproject.repository.jpa.music.profile.ProfileMusic;
import com.github.musicsnsproject.repository.jpa.music.profile.ProfileMusicRepository;
import com.github.musicsnsproject.repository.jpa.music.purchase.PurchaseMusicRepository;
import com.github.musicsnsproject.repository.spotify.SpotifyDao;
import com.github.musicsnsproject.web.dto.profile.ProfileMusicResponse;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import se.michaelthelin.spotify.model_objects.specification.Track;

import static com.github.musicsnsproject.repository.jpa.emotion.QUserEmotion.userEmotion;
import static com.github.musicsnsproject.repository.jpa.emotion.QEmotion.emotion;
import static com.github.musicsnsproject.repository.jpa.account.user.QMyUser.myUser;
import static com.github.musicsnsproject.repository.jpa.music.profile.QProfileMusic.profileMusic;
import static com.github.musicsnsproject.repository.jpa.music.QMyMusic.myMusic;
import static com.github.musicsnsproject.repository.jpa.music.purchase.QPurchaseHistory.purchaseHistory;
import static com.github.musicsnsproject.repository.jpa.music.purchase.QPurchaseMusic.purchaseMusic;

import java.sql.SQLException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {

    private final ProfileMusicRepository profileMusicRepository;
    private final PurchaseMusicRepository purchaseMusicRepository;
    private final MyMusicRepository myMusicRepository;
    private final UserEmotionRepository userEmotionRepository;
    private final JPAQueryFactory jpaQueryFactory;
    private final SpotifyDao spotifyDao;


    // 프로필 음악리스트 가져오기
    @Override
    public List<ProfileMusicResponse> getProfileMusicList(Long userId, String musicId) {
        return List.of();
    }


    // 프로필 음악 추가하기
    @Override
    @Transactional
    public void addProfileMusic(Long userId, String musicId, Long emotionId) {

        Track tracks = spotifyDao.findTrackById(musicId);

        // 프로필 음악 list order 가져오기 (null 처리)
        Integer listOrder = jpaQueryFactory
                .select(profileMusic.listOrder.max())
                .from(profileMusic)
                .join(profileMusic.userEmotion, userEmotion)
                .join(userEmotion.myUser, myUser)
                .where(myUser.userId.eq(userId))
                .fetchOne();

        int nextOrder = (listOrder == null ? 0 : listOrder + 1);

        if (nextOrder > 10) {
            throw CustomNotAcceptException.of()
                    .customMessage("프로필 음악은 10개만 설정 가능합니다.")
                    .request(nextOrder)
                    .build();
        }

        // 해당 사용자의 myMusicId 구하기
        Long myMusicId = jpaQueryFactory.select(myMusic.myMusicId)
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

        if (myMusicId == null) {
            throw CustomNotAcceptException.of()
                    .customMessage("해당 음악이 내 음악에 없습니다.")
                    .request(myMusicId)
                    .build();
        }

        MyMusic myMusicRef = myMusicRepository.getReferenceById(myMusicId);
        UserEmotion userEmotionRef = userEmotionRepository.getReferenceById(emotionId);

        ProfileMusic profileMusic = ProfileMusic.builder()
                .myMusic(myMusicRef)
                .userEmotion(userEmotionRef)
                .listOrder(nextOrder)
                .build();

        profileMusicRepository.save(profileMusic);

    }
}
