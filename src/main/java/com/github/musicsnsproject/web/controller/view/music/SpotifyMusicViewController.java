package com.github.musicsnsproject.web.controller.view.music;

import com.github.musicsnsproject.common.myenum.MusicSearchType;
import com.github.musicsnsproject.service.music.SpotifyMusicService;
import com.github.musicsnsproject.web.dto.music.spotify.track.TrackResponse;
import com.github.musicsnsproject.web.dto.pageable.PaginationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/music")
@RequiredArgsConstructor
public class SpotifyMusicViewController {
    private final SpotifyMusicService spotifyMusicService;

    @GetMapping("/search")
    public String searchBySpotify(
            @RequestParam String keyword,
            @RequestParam MusicSearchType searchType,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size
    ){
        return "music/search";
    }
}
