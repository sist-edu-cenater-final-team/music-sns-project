package com.github.musicsnsproject.web.dto.music.spotify.track;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.musicsnsproject.web.dto.music.spotify.artist.SimplifiedArtist;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class SimplifiedTrack {
    private final String trackId;
    private final String trackName;
    private final String duration;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private final Integer durationMs;
    private final String trackSpotifyUrl;
    private final int trackNumber;
    private final String trackType;
    private final List<SimplifiedArtist> artists;
}