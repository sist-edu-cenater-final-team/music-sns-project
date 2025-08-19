package com.github.musicsnsproject.web.dto.music.spotify.track;

import com.github.musicsnsproject.web.dto.music.spotify.album.SimplifiedAlbum;
import com.github.musicsnsproject.web.dto.music.spotify.artist.SimplifiedArtist;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
@Getter
@Builder
public class TrackResponseV1 {
    private final String trackId;
    private final String trackName;
    private final String duration;
    private final String trackSpotifyUrl;
    private final int trackPopularity;
    private final int trackNumber;
    private final List<SimplifiedArtist> artist;
    private final SimplifiedAlbum album;
}
