package com.github.musicsnsproject.web.dto.music.spotify.track;

import com.github.musicsnsproject.web.dto.music.spotify.album.SimplifiedAlbum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor(staticName = "of")
@Getter
public class TrackResponse {
    private final SimplifiedTrack track;
    private final SimplifiedAlbum album;
    private final int popularity;
}
