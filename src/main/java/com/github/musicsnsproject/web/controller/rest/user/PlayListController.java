package com.github.musicsnsproject.web.controller.rest.user;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.github.musicsnsproject.domain.ProfileMusicVO;
import com.github.musicsnsproject.service.music.SpotifyMusicService;
import com.github.musicsnsproject.service.musicplaylist.MusicPlayListService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/music")
@RequiredArgsConstructor
public class PlayListController {

	private final MusicPlayListService service;
	private final SpotifyMusicService musicSerivce;
	
	@GetMapping("/palyList")
	public List<ProfileMusicVO> playList(@RequestParam("emotionId") Long emotionId ) {
		
		return service.emotionPlayList(emotionId);
	}
	
	
	
}
