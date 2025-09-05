package com.github.musicsnsproject.repository.spotify.wrapper.album;

import lombok.AllArgsConstructor;
import se.michaelthelin.spotify.enums.AlbumType;
import se.michaelthelin.spotify.model_objects.specification.Album;
import se.michaelthelin.spotify.model_objects.specification.ArtistSimplified;
import se.michaelthelin.spotify.model_objects.specification.ExternalUrl;
import se.michaelthelin.spotify.model_objects.specification.Image;

@AllArgsConstructor(staticName = "of")
public class AlbumWrapper implements  AlbumBase{
    private final Album album;
    @Override
    public String getId() { return album.getId(); }

    @Override
    public String getName() { return album.getName(); }

    @Override
    public ExternalUrl getExternalUrls() { return album.getExternalUrls(); }

    @Override
    public Image[] getImages() { return album.getImages(); }

    @Override
    public String getReleaseDate() { return album.getReleaseDate(); }

    @Override
    public AlbumType getType() { return album.getAlbumType(); }

    @Override
    public ArtistSimplified[] getArtists() { return album.getArtists(); }

}