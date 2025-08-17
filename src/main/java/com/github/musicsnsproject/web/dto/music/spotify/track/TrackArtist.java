package com.github.musicsnsproject.web.dto.music.spotify.track;

public record TrackArtist(String artistId, String artistName, String artistSpotifyUrl) {
    public static TrackArtist of(String artistId, String artistName, String artistSpotifyUrl) {
        return new TrackArtist(artistId, artistName, artistSpotifyUrl);
    }
}
