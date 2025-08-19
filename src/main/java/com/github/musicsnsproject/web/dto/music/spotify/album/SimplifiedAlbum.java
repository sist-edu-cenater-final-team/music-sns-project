package com.github.musicsnsproject.web.dto.music.spotify.album;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.github.musicsnsproject.web.dto.music.spotify.artist.SimplifiedArtist;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Builder
public class SimplifiedAlbum {
    private final String albumId;
    private final String albumName;
    private final String albumSpotifyUrl;
    private final String albumImageUrl;
    private final LocalDate releaseDate;
    private final String albumType;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private final List<SimplifiedArtist> artists;
}