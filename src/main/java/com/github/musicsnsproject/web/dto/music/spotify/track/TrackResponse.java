package com.github.musicsnsproject.web.dto.music.spotify.track;

import lombok.Builder;

import java.time.LocalDate;
import java.util.List;

@Builder
public class TrackResponse {
    private final String trackId;
    private final String trackName;
    private final String duration;
    private final String trackSpotifyUrl;
    private final int trackPopularity;
    private final int trackNumber;
    private final List<TrackArtist> artist;
    private final TrackAlbum album;

    @Builder
    public static class TrackAlbum {
        private final String albumId;
        private final String albumName;
        private final String albumSpotifyUrl;
        private final String albumImageUrl;
        private final LocalDate releaseDate;
        private final String albumType;
    }
}
