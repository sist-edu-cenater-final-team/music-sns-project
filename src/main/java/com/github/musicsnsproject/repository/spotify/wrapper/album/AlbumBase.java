package com.github.musicsnsproject.repository.spotify.wrapper.album;

import se.michaelthelin.spotify.enums.AlbumType;
import se.michaelthelin.spotify.model_objects.specification.ArtistSimplified;
import se.michaelthelin.spotify.model_objects.specification.ExternalUrl;
import se.michaelthelin.spotify.model_objects.specification.Image;

public interface AlbumBase {
    String getId();
    String getName();
    ExternalUrl getExternalUrls();
    Image[] getImages();
    String getReleaseDate();
    AlbumType getType();
    ArtistSimplified[] getArtists();
}