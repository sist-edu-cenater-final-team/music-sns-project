package com.github.musicsnsproject.web.dto.music.spotify;

public record SimplifiedArtist(
        String artistId,
        String artistName,
        String artistSpotifyUrl
) {
    public static SimplifiedArtist of(String artistId, String artistName, String artistSpotifyUrl) {
        return new SimplifiedArtist(artistId, artistName, artistSpotifyUrl);
    }
}
