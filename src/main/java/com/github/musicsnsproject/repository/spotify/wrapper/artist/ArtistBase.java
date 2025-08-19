package com.github.musicsnsproject.repository.spotify.wrapper.artist;

import se.michaelthelin.spotify.enums.ModelObjectType;
import se.michaelthelin.spotify.model_objects.specification.ExternalUrl;

public interface ArtistBase {
    String getId();
    String getName();
    ExternalUrl getExternalUrls();
    ModelObjectType getType();
}
