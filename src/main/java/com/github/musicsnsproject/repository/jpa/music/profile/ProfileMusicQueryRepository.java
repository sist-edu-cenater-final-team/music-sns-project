package com.github.musicsnsproject.repository.jpa.music.profile;

import java.util.List;

import com.github.musicsnsproject.domain.ProfileMusicVO;

public interface ProfileMusicQueryRepository {

	List<ProfileMusicVO> emotionPlayList(Long emotionId);
}
