package com.github.musicsnsproject.web.dto.music.spotify.search;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
@Getter
@AllArgsConstructor(staticName = "of")
public class RecommendSearch {
    private List<String> trackNames;
    private List<RecommendSong> recommendSongs;
    @Getter
    @AllArgsConstructor(staticName = "of")
    public static class RecommendSong {
        private String trackSpotifyUrl;
        private String trackName;
        private List<RecommendArtist> artists;
        private String albumId;
        private String albumImageUrl;

    }
    @Getter
    @AllArgsConstructor(staticName = "of")
    public static class RecommendArtist {
        private String artistId;
        private String artistName;
    }
}
