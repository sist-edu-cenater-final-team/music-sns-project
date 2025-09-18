package com.github.musicsnsproject.web.dto.music.spotify.artist;

public record SimplifiedArtist(
        String artistId,
        String artistName,
        String artistSpotifyUrl,
        String artistType
) {
    public static SimplifiedArtist of(String artistId, String artistName, String artistSpotifyUrl, String artistType) {
        return new SimplifiedArtist(artistId, artistName, artistSpotifyUrl, artistType);
    }
}
