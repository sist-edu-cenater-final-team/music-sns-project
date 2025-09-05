package com.github.musicsnsproject.service.music.profile;

import com.github.musicsnsproject.common.myenum.EmotionEnum;
import com.github.musicsnsproject.web.dto.profile.ProfileMusicResponse;

import java.util.List;

public interface ProfileMusicService {

    // 프로필 음악 가져오기
    List<ProfileMusicResponse> getProfileMusicList(Long userId);

    // 프로필 음악 추가하기
    List<ProfileMusicResponse> addProfileMusic(Long userId, String musicId, EmotionEnum emotion);

    // 프로필 음악 삭제하기
    void deleteProfileMusic(Long userId, String musicId);
}
