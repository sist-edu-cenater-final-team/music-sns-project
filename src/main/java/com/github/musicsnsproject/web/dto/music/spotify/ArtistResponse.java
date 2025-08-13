package com.github.musicsnsproject.web.dto.music.spotify;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ArtistResponse {
    public final String artistId;
    public final String artistName;
    public final String artistSpotifyUrl;

}
