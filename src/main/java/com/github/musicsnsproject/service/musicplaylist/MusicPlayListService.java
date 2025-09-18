package com.github.musicsnsproject.service.musicplaylist;

import java.util.List;

import com.github.musicsnsproject.domain.ProfileMusicVO;
import com.github.musicsnsproject.web.dto.music.spotify.track.TrackResponse;

public interface MusicPlayListService {

	List<ProfileMusicVO> emotionPlayList(Long emotionId);

	List<TrackResponse> musicInfo(List<String> musicId);

	List<ProfileMusicVO> profileMusicId(Long userId);

}
