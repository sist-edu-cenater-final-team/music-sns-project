package com.github.musicsnsproject.web.controller.rest.music;

import com.github.musicsnsproject.service.music.SpotifyMusicService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/music/spotify")
@RequiredArgsConstructor
public class SpotifyMusicController {
    private final SpotifyMusicService spotifyMusicService;


}
