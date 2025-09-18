package com.github.musicsnsproject.web.dto.music.spotify.album;

import com.github.musicsnsproject.web.dto.music.spotify.track.SimplifiedTrack;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class AlbumResponse {
    private final SimplifiedAlbum album;
    private final List<String> genres;
    private final List<String> copyrights;
    private final String label;
    private final List<SimplifiedTrack> tracks;
    private final int popularity;
}
