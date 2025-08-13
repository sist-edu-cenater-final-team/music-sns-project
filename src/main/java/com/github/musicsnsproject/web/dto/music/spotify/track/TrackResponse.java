package com.github.musicsnsproject.web.dto.music.spotify.track;

import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDate;
import java.util.List;

@Builder
public record TrackResponse(String trackId, String trackName, String duration, String trackSpotifyUrl, int trackPopularity,
                            int trackNumber, List<TrackArtist> artist, TrackAlbum album) {
    @Builder
        public record TrackAlbum(String albumId, String albumName, String albumSpotifyUrl, String albumImageUrl,
                                 LocalDate releaseDate, String albumType) {
    }
}
