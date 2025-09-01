package com.github.musicsnsproject.service.musicplaylist;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.github.musicsnsproject.domain.ProfileMusicVO;
import com.github.musicsnsproject.repository.jpa.account.user.MyUserRepository;
import com.github.musicsnsproject.repository.jpa.music.profile.ProfileMusicQueryRepository;
import com.github.musicsnsproject.service.music.SpotifyMusicService;
import com.github.musicsnsproject.web.dto.music.spotify.track.TrackResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MusicPlayListService_imple implements MusicPlayListService {

	private final ProfileMusicQueryRepository repository;
	private final SpotifyMusicService musicService;
	
	@Override
	public List<ProfileMusicVO> emotionPlayList(Long emotionId) {
		return repository.emotionPlayList(emotionId);
	}



	@Override
	public List<ProfileMusicVO> profileMusicId(Long userId) {
		
		return repository.profileMusicId(userId);
	}

	@Override
	public List<TrackResponse> musicInfo(List<String> musicId) {
		List<TrackResponse> musicList = new ArrayList<>();
		for(int i = 0; i < musicId.size(); i++) {
			musicList.add(musicService.getTrackResponseV2ById(musicId.get(i)));
		}
		return musicList;
	}
}
