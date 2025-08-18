package com.github.musicsnsproject.web.dto.music.spotify.album;

import com.github.musicsnsproject.web.dto.music.spotify.SimplifiedAlbum;
import com.github.musicsnsproject.web.dto.music.spotify.SimplifiedArtist;
import lombok.Getter;

import java.util.List;


public record AlbumResponse(
        SimplifiedAlbum album,
        List<SimplifiedArtist> artists,
        int popularity
        ) {
    public static AlbumResponse of(SimplifiedAlbum album, List<SimplifiedArtist> artists) {
        return new AlbumResponse(album, artists,0);
    }
    public static AlbumResponse of(SimplifiedAlbum album, List<SimplifiedArtist> artists, int popularity) {
        return new AlbumResponse(album, artists, popularity);
    }
}
