package com.github.musicsnsproject.web.controller.rest.user;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.github.musicsnsproject.domain.ProfileMusicVO;
import com.github.musicsnsproject.service.musicplaylist.MusicPlayListService;
import com.github.musicsnsproject.web.dto.music.spotify.track.TrackResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/music")
@RequiredArgsConstructor
public class PlayListController {

	private final MusicPlayListService service;

	
	@GetMapping("/palyList")
	public List<ProfileMusicVO> playList(@RequestParam("emotionId") Long emotionId ) {
		return service.emotionPlayList(emotionId);
	}
	
	@GetMapping("/musicList")
	public List<TrackResponse> musicList(@RequestParam("musicId") List<String> musicId) {
		return service.musicInfo(musicId);  
	}
	
	@GetMapping("/myProfileMusic")
	public List<ProfileMusicVO> profileMusic(@AuthenticationPrincipal Long userId) {
			//Long fakeUserId = (long) 44;
		return service.profileMusicId(userId);
	}
	
	
}
