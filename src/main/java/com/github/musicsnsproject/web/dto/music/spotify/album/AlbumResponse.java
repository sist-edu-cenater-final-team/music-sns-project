package com.github.musicsnsproject.web.dto.music.spotify.album;

import com.github.musicsnsproject.web.dto.music.spotify.SimplifiedAlbum;
import com.github.musicsnsproject.web.dto.music.spotify.SimplifiedArtist;
import lombok.Getter;

import java.util.List;


public record AlbumResponse(
        SimplifiedAlbum album,
        List<SimplifiedArtist> artists
        ) {
    public static AlbumResponse of(SimplifiedAlbum album, List<SimplifiedArtist> artists) {
        return new AlbumResponse(album, artists);
    }
}
