package com.github.musicsnsproject.web.dto.music.spotify.artist;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class ArtistResponse {
    public final String artistId;
    public final String artistName;
    public final String artistSpotifyUrl;
    private final List<String> artistGenres;
    private final int totalFollowers;
    private final String artistImageUrl;
    private final int artistPopularity;

}
