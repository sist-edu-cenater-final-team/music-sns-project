package com.github.musicsnsproject.service.music.profile;

import com.github.musicsnsproject.repository.jpa.music.profile.ProfileMusicRepository;
import com.github.musicsnsproject.repository.jpa.music.purchase.PurchaseMusicRepository;
import com.github.musicsnsproject.repository.spotify.SpotifyDao;
import com.github.musicsnsproject.web.dto.profile.ProfileMusicResponse;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import se.michaelthelin.spotify.model_objects.specification.Track;

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
    public void addProfileMusic(Long userId, String musicId) {

        Track tracks = spotifyDao.findTrackById(musicId);

    }
}
