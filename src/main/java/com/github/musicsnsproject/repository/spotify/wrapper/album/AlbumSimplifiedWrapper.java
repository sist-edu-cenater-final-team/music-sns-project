package com.github.musicsnsproject.repository.spotify.wrapper.album;

import lombok.AllArgsConstructor;
import se.michaelthelin.spotify.enums.AlbumType;
import se.michaelthelin.spotify.model_objects.specification.AlbumSimplified;
import se.michaelthelin.spotify.model_objects.specification.ArtistSimplified;
import se.michaelthelin.spotify.model_objects.specification.ExternalUrl;
import se.michaelthelin.spotify.model_objects.specification.Image;

@AllArgsConstructor(staticName = "of")
public class AlbumSimplifiedWrapper implements AlbumBase {
    private final AlbumSimplified albumSimplified;

    @Override
    public String getId() { return albumSimplified.getId(); }

    @Override
    public String getName() { return albumSimplified.getName(); }

    @Override
    public ExternalUrl getExternalUrls() { return albumSimplified.getExternalUrls(); }

    @Override
    public Image[] getImages() { return albumSimplified.getImages(); }

    @Override
    public String getReleaseDate() { return albumSimplified.getReleaseDate(); }

    @Override
    public AlbumType getType() {
        return albumSimplified.getAlbumType();
    }


    @Override
    public ArtistSimplified[] getArtists() { return albumSimplified.getArtists(); }
}