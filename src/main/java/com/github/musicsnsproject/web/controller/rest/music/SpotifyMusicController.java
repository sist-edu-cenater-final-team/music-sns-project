package com.github.musicsnsproject.web.controller.rest.music;

import com.github.musicsnsproject.common.myenum.MusicSearchType;
import com.github.musicsnsproject.service.music.SpotifyMusicService;
import com.github.musicsnsproject.web.dto.music.spotify.track.TrackResponse;
import com.github.musicsnsproject.web.dto.pageable.PaginationResponse;
import com.github.musicsnsproject.web.dto.response.CustomSuccessResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/music/spotify")
@RequiredArgsConstructor
public class SpotifyMusicController {
    private final SpotifyMusicService spotifyMusicService;
    @GetMapping("/search")
    public CustomSuccessResponse<PaginationResponse<TrackResponse>> searchTrackListSpotify(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "아티스트") MusicSearchType searchType,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size){
        PaginationResponse<TrackResponse> response =  spotifyMusicService.searchTracksRequest(keyword, searchType, page-1, size);
        return CustomSuccessResponse.ofOk("Spotify 트랙 검색 성공", response);
    }
    @GetMapping("/chart")
    public Object test (@RequestParam String playListId){
        return spotifyMusicService.getSpotifyChart(playListId);
    }


}
