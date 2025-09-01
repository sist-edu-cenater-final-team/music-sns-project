package com.github.musicsnsproject.service.music.profile;

import com.github.musicsnsproject.web.dto.profile.ProfileMusicResponse;

import java.util.List;

public interface ProfileService {

    // 프로필 음악 가져하기
    List<ProfileMusicResponse> getProfileMusicList(Long userId, String musicId);

    // 프로필 음악 추가하기
    void addProfileMusic(Long userId, String musicId, Long emotionId);
}
