package com.github.musicsnsproject.web.controller.rest.music;

import com.github.musicsnsproject.common.myenum.MusicSearchType;
import com.github.musicsnsproject.service.music.SpotifyMusicService;
import com.github.musicsnsproject.web.dto.music.spotify.album.SimplifiedAlbum;
import com.github.musicsnsproject.web.dto.music.spotify.album.AlbumResponse;
import com.github.musicsnsproject.web.dto.music.spotify.artist.ArtistResponse;
import com.github.musicsnsproject.web.dto.music.spotify.track.TrackResponse;
import com.github.musicsnsproject.web.dto.music.spotify.track.TrackResponseV1;
import com.github.musicsnsproject.web.dto.pageable.ScrollResponse;
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
    public CustomSuccessResponse<ScrollResponse<TrackResponse>> searchTrackListSpotify(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "아티스트") MusicSearchType searchType,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        ScrollResponse<TrackResponse> response = spotifyMusicService.searchTracksRequest(keyword, searchType, page - 1, size);
        return CustomSuccessResponse.ofOk("Spotify 트랙 검색 성공", response);
    }
//    @GetMapping("/chart")
//    public Object test (@RequestParam String playListId){
//        return spotifyMusicService.getSpotifyChart(playListId);
//    }

    @GetMapping("/artist")
    public CustomSuccessResponse<ArtistResponse> searchArtistById(@RequestParam String artistId) {
        ArtistResponse response = spotifyMusicService.searchArtistById(artistId);
        return CustomSuccessResponse.ofOk("Spotify 아티스트 조회 성공", response);
    }
    @GetMapping("/album")
    public CustomSuccessResponse<AlbumResponse> searchAlbumById(@RequestParam String albumId) {
        AlbumResponse response = spotifyMusicService.searchAlbumById(albumId);
        return CustomSuccessResponse.ofOk("Spotify 앨범 조회 성공", response);
    }

    @GetMapping("/artist/albums")
    public CustomSuccessResponse<ScrollResponse<SimplifiedAlbum>> searchArtistAlbums(@RequestParam String artistId,
                                                                                     @RequestParam(defaultValue = "1") int page,@RequestParam(defaultValue = "10") int size) {
        ScrollResponse<SimplifiedAlbum> artistAlbums = spotifyMusicService.searchArtistAlbums(artistId, page - 1, size);
        return CustomSuccessResponse.ofOk("Spotify 아티스트 앨범 조회 성공", artistAlbums);
    }


}
