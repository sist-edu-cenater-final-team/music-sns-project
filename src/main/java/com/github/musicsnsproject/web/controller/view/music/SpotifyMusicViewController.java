package com.github.musicsnsproject.web.controller.view.music;

import com.github.musicsnsproject.common.myenum.MusicSearchType;
import com.github.musicsnsproject.service.music.SpotifyMusicService;
import com.github.musicsnsproject.web.dto.music.spotify.track.TrackResponse;
import com.github.musicsnsproject.web.dto.pageable.PaginationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
            Model model
    ){
        model.addAttribute("boot", "5.3.2");
        return "music/search";
    }
    @GetMapping("/artist/{artistId}")
    public String searchArtistById( @PathVariable String artistId ){
        return "music/artist";
    }
}
