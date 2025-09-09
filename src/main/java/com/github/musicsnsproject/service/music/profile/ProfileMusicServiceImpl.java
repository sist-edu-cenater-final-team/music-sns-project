package com.github.musicsnsproject.service.music.profile;

import com.github.musicsnsproject.common.exceptions.CustomNotAcceptException;
import com.github.musicsnsproject.common.myenum.EmotionEnum;
import com.github.musicsnsproject.repository.jpa.account.user.MyUser;
import com.github.musicsnsproject.repository.jpa.emotion.Emotion;
import com.github.musicsnsproject.repository.jpa.emotion.UserEmotion;
import com.github.musicsnsproject.repository.jpa.emotion.UserEmotionRepository;
import com.github.musicsnsproject.repository.jpa.music.MyMusic;
import com.github.musicsnsproject.repository.jpa.music.MyMusicRepository;
import com.github.musicsnsproject.repository.jpa.music.QMyMusic;
import com.github.musicsnsproject.repository.jpa.music.profile.ProfileMusic;
import com.github.musicsnsproject.repository.jpa.music.profile.ProfileMusicRepository;
import com.github.musicsnsproject.repository.jpa.music.profile.QProfileMusic;
import com.github.musicsnsproject.repository.jpa.music.purchase.PurchaseMusicRepository;
import com.github.musicsnsproject.repository.jpa.music.purchase.QPurchaseHistory;
import com.github.musicsnsproject.repository.jpa.music.purchase.QPurchaseMusic;
import com.github.musicsnsproject.repository.spotify.SpotifyDao;
import com.github.musicsnsproject.web.dto.profile.ProfileMusicResponse;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import se.michaelthelin.spotify.model_objects.specification.ArtistSimplified;
import se.michaelthelin.spotify.model_objects.specification.Track;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProfileMusicServiceImpl implements ProfileMusicService {

    private final ProfileMusicRepository profileMusicRepository;
    private final PurchaseMusicRepository purchaseMusicRepository;
    private final UserEmotionRepository userEmotionRepository;
    private final MyMusicRepository myMusicRepository;
    private final JPAQueryFactory jpaQueryFactory;
    private final SpotifyDao spotifyDao;


    @Override
    // 프로필 음악리스트 가져오기
    public List<ProfileMusicResponse> getProfileMusicList(Long userId) {

        //
        List<ProfileMusic> profileMusics = profileMusicRepository.findMyMusics(userId);

        if (profileMusics.isEmpty()) return Collections.emptyList();

        // 프로필 음악에 있는 musicId 가져오기
        List<String> musicIds = profileMusics.stream()
                .map(ProfileMusic::getMusicId)
                .distinct()
                .toList();

        // Track 정보 조회
        Track[] tracks = spotifyDao.findAllTrackByIds(musicIds);

        // DTO 변환 (중복 제거)
        Map<String, Track> trackMap = Arrays.stream(tracks)
                .distinct()
                .collect(Collectors.toMap(Track::getId, track -> track));

        // 5. ProfileMusicResponse 생성
        List<ProfileMusicResponse> responses = profileMusics.stream()
                .map(pm -> {
                    String musicId = pm.getMusicId();
                    Track track = trackMap.get(musicId);
                    if (track == null) return null;

                    return ProfileMusicResponse.builder()
                            .musicId(musicId)
                            .musicName(track.getName()) // track 이름
                            .artistName(Arrays.stream(track.getArtists())
                                    .map(ArtistSimplified::getName)
                                    .collect(Collectors.joining(", ")))
                            .albumImageUrl(track.getAlbum().getImages()[0].getUrl())
                            .listOrder(pm.getListOrder())
                            .build();

                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        return responses;
    }

    // 프로필 음악 추가하기
    @Override
    @Transactional
    public List<ProfileMusicResponse> addProfileMusic(Long userId, String musicId, EmotionEnum emotion) {

        // 중복 체크 (musicId 기준)
        if (profileMusicRepository.duplicateCheck(userId, musicId)) {
            throw CustomNotAcceptException.of()
                    .customMessage("이미 설정한 음악입니다.")
                    .request(musicId)
                    .build();
        }

        // 사용자의 프로필에 등록된 음악 조회하기
        List<Long> myMusicIds = profileMusicRepository.findMyMusicIdsByUserId(userId);
        int nextOrder = myMusicIds.size();

        if (nextOrder >= 10) {
            throw CustomNotAcceptException.of()
                    .customMessage("프로필 음악은 10개만 설정 가능합니다.")
                    .request(nextOrder)
                    .build();
        }

        // PurchaseHistory MyMusic 찾기
        Long myMusicId = purchaseMusicRepository.findOneMyMusicIdByMusicId(userId, musicId);

        // null 체크 추가
        if (myMusicId == null) {
            throw CustomNotAcceptException.of()
                    .customMessage("해당 userId와 musicId에 해당하는 MyMusic이 존재하지 않습니다.")
                    .request(myMusicId)
                    .build();
        }
        MyMusic myMusic = myMusicRepository.findById(myMusicId)
                .orElseThrow(() -> new IllegalStateException("음악을 찾을 수 없습니다."));

        // UserEmotion 생성
        Emotion myEmotion = Emotion.fromEmotionValue(emotion);
        MyUser user = MyUser.onlyId(userId);
        UserEmotion userEmotion = UserEmotion.fromUserEmotion(myEmotion, user);
        userEmotionRepository.save(userEmotion);

        // ProfileMusic 저장
        ProfileMusic profileMusic = ProfileMusic.builder()
                .myMusic(myMusic)
                .musicId(musicId)
                .userEmotion(userEmotion)
                .listOrder(nextOrder)
                .build();

        profileMusicRepository.save(profileMusic);

        return getProfileMusicList(userId);
    }

    // 프로필 음악 삭제하기
    @Override
    @Transactional
    public void deleteProfileMusic(Long userId, String musicId) {

        // 삭제할 프로필 음악 찾기
        ProfileMusic profileMusic = profileMusicRepository.findDeleteByMusicId(userId, musicId);

        if(profileMusic == null) {
            throw CustomNotAcceptException.of()
                    .customMessage("삭제할 프로필 음악이 없습니다.")
                    .request(musicId)
                    .build();
        }

        // 삭제 전 listOrder 가져오기
        int deleteListOrder = profileMusic.getListOrder();

        // 삭제하기 실행
        profileMusicRepository.delete(profileMusic);

        // 순서 재조정
        updateListOrder(userId, deleteListOrder);
    }


    // 순서 재조정하기
    private void updateListOrder(Long userId, int deletedOrder) {
        List<ProfileMusic> updateList = profileMusicRepository.findAllAfterDelete(userId, deletedOrder);

        updateList.forEach(ProfileMusic::decrementListOrder);
    }

}
