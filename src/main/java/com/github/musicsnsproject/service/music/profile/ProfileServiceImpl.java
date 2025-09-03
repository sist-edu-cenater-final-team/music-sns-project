package com.github.musicsnsproject.service.music.profile;

import com.github.musicsnsproject.common.exceptions.CustomNotAcceptException;
import com.github.musicsnsproject.common.myenum.EmotionEnum;
import com.github.musicsnsproject.repository.jpa.account.user.MyUser;
import com.github.musicsnsproject.repository.jpa.emotion.Emotion;
import com.github.musicsnsproject.repository.jpa.emotion.UserEmotion;
import com.github.musicsnsproject.repository.jpa.emotion.UserEmotionRepository;
import com.github.musicsnsproject.repository.jpa.music.MyMusic;
import com.github.musicsnsproject.repository.jpa.music.MyMusicRepository;
import com.github.musicsnsproject.repository.jpa.music.profile.ProfileMusic;
import com.github.musicsnsproject.repository.jpa.music.profile.ProfileMusicRepository;
import com.github.musicsnsproject.repository.jpa.music.purchase.PurchaseMusicRepository;
import com.github.musicsnsproject.repository.spotify.SpotifyDao;
import com.github.musicsnsproject.service.musicplaylist.MusicPlayListService;
import com.github.musicsnsproject.web.dto.profile.ProfileMusicResponse;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import se.michaelthelin.spotify.model_objects.specification.ArtistSimplified;
import se.michaelthelin.spotify.model_objects.specification.Track;

import static com.github.musicsnsproject.repository.jpa.emotion.QUserEmotion.userEmotion;
import static com.github.musicsnsproject.repository.jpa.account.user.QMyUser.myUser;
import static com.github.musicsnsproject.repository.jpa.music.profile.QProfileMusic.profileMusic;
import static com.github.musicsnsproject.repository.jpa.music.QMyMusic.myMusic;
import static com.github.musicsnsproject.repository.jpa.music.purchase.QPurchaseHistory.purchaseHistory;
import static com.github.musicsnsproject.repository.jpa.music.purchase.QPurchaseMusic.purchaseMusic;

import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {

    private final ProfileMusicRepository profileMusicRepository;
    private final PurchaseMusicRepository purchaseMusicRepository;
    private final MyMusicRepository myMusicRepository;
    private final UserEmotionRepository userEmotionRepository;
    private final JPAQueryFactory jpaQueryFactory;
    private final SpotifyDao spotifyDao;
    private final MusicPlayListService service;


    // 프로필 음악리스트 가져오기
    @Override
    public List<ProfileMusicResponse> getProfileMusicList(Long userId, String musicId) {


        // 받아온 musicId 리스트로 만들기
        List<String> musicIds = profileMusicRepository.getAddMusicId(userId, musicId);

        // 프로필 설정된 musicId들로 spotify Track 배열 조회하기
        Track[] tracks = spotifyDao.findAllTrackByIds(musicIds);

        // 조회한 Track 배열 Map으로 변환하기
        Map<String, Track> trackMap = Arrays.stream(tracks)
                .collect(Collectors.toMap(Track::getId, track -> track));

        List<ProfileMusicResponse> list = musicIds.stream()
                .map(pm -> {

                    Track track = trackMap.get(pm);

                    String trackName = track.getName();
                    String albumImageUrl = track.getAlbum().getImages()[0].getUrl();

                    String artistName = Arrays.stream(track.getArtists())
                            .map(ArtistSimplified::getName)
                            .distinct()
                            .collect(Collectors.joining(", "));


                    return ProfileMusicResponse.builder()
                            .musicId(pm)
                            .musicName(trackName)
                            .artistName(artistName)
                            .albumImageUrl(albumImageUrl)
                            .build();


                })
                .toList();



        return list;
    }


    // 프로필 음악 추가하기
    @Override
    @Transactional
    public void addProfileMusic(Long userId, String musicId, EmotionEnum emotion) {

        // 프로필 음악 list order 가져오기
        Integer listOrder = jpaQueryFactory
                .select(profileMusic.listOrder.max())
                .from(profileMusic)
                .join(profileMusic.userEmotion, userEmotion)
                .join(userEmotion.myUser, myUser)
                .where(myUser.userId.eq(userId))
                .fetchOne();

        int nextOrder = (listOrder == null ? 0 : listOrder + 1);

        if (nextOrder >= 10) {
            throw CustomNotAcceptException.of()
                    .customMessage("프로필 음악은 10개만 설정 가능합니다.")
                    .request(nextOrder)
                    .build();
        }

        // 해당 사용자의 myMusicId 구하기
        Long myMusicId = profileMusicRepository.findMyMusicId(userId, musicId);

        // 중복체크
        boolean duplicateCheck = profileMusicRepository.duplicateCheck(userId, musicId, myMusicId);

        if(duplicateCheck){
            throw CustomNotAcceptException.of()
                    .customMessage("이미 설정한 음악입니다.")
                    .request(musicId)
                    .build();
        }

        Emotion myEmotion = Emotion.fromEmotionValue(emotion);
        MyUser user = MyUser.onlyId(userId);

        UserEmotion userEmotion = UserEmotion.fromUserEmotion(myEmotion, user);
        userEmotionRepository.save(userEmotion);

        MyMusic myMusicRef = myMusicRepository.getReferenceById(myMusicId);


        ProfileMusic profileMusic = ProfileMusic.builder()
                .myMusic(myMusicRef)
                .userEmotion(userEmotion)
                .listOrder(nextOrder)
                .build();

        profileMusicRepository.save(profileMusic);

    }
}
