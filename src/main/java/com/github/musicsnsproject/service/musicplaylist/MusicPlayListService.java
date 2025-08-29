package com.github.musicsnsproject.service.musicplaylist;

import java.util.List;

import com.github.musicsnsproject.domain.ProfileMusicVO;

public interface MusicPlayListService {

	List<ProfileMusicVO> emotionPlayList(Long emotionId);

}
