package com.github.musicsnsproject.service.music.profile;

import com.github.musicsnsproject.common.exceptions.CustomNotAcceptException;
import com.github.musicsnsproject.repository.jpa.account.user.MyUser;
import com.github.musicsnsproject.repository.jpa.emotion.Emotion;
import com.github.musicsnsproject.repository.jpa.emotion.UserEmotion;
import com.github.musicsnsproject.repository.jpa.music.MyMusic;
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
import static com.github.musicsnsproject.repository.jpa.music.profile.QProfileMusic.profileMusic;
import static com.github.musicsnsproject.repository.jpa.account.user.QMyUser.myUser;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {

    private final ProfileMusicRepository profileMusicRepository;
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

        // 프로필 음악 list order 가져오기
        int listOrder = jpaQueryFactory
                .select(profileMusic.listOrder.max())
                .from(profileMusic)
                .join(profileMusic.userEmotion, userEmotion)
                .join(userEmotion.myUser, myUser)
                .where(myUser.userId.eq(userId))
                .fetchOne();

        int nextOrder = (listOrder < 0 ? 0 : listOrder + 1);

        if(nextOrder > 10){
            throw CustomNotAcceptException.of()
                    .customMessage("프로필 음악은 10개만 설정 가능합니다.")
                    .request(nextOrder)
                    .build();
        }

//        MyMusic myMusic = MyMusic.

//        ProfileMusic profileMusic = ProfileMusic.builder()
//                .myMusic()
//                        .listOrder(nextOrder)
//                                .build();



        System.out.println("listOrder : " + listOrder);


    }
}
