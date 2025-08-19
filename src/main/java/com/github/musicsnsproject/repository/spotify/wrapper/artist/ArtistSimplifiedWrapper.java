package com.github.musicsnsproject.repository.spotify.wrapper.artist;

import lombok.AllArgsConstructor;
import se.michaelthelin.spotify.enums.ModelObjectType;
import se.michaelthelin.spotify.model_objects.specification.ArtistSimplified;
import se.michaelthelin.spotify.model_objects.specification.ExternalUrl;

@AllArgsConstructor(staticName = "of")
public class ArtistSimplifiedWrapper implements ArtistBase {
    private final ArtistSimplified artist;

    @Override
    public String getId() {
        return artist.getId();
    }

    @Override
    public String getName() {
        return artist.getName();
    }

    @Override
    public ExternalUrl getExternalUrls() {
        return artist.getExternalUrls();
    }

    @Override
    public ModelObjectType getType() {
        return artist.getType();
    }
}
