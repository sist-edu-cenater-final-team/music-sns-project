package com.github.musicsnsproject.web.dto.music.spotify.track;


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
    private final String trackSpotifyUrl;
    private final int trackNumber;
    private final String trackType;
    private final List<SimplifiedArtist> artists;
}