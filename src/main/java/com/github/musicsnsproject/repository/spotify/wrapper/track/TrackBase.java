package com.github.musicsnsproject.repository.spotify.wrapper.track;

import se.michaelthelin.spotify.enums.ModelObjectType;
import se.michaelthelin.spotify.model_objects.specification.AlbumSimplified;
import se.michaelthelin.spotify.model_objects.specification.ArtistSimplified;
import se.michaelthelin.spotify.model_objects.specification.ExternalUrl;

public interface TrackBase {
    String getId();
    String getName();
    int getDurationMs();
    ArtistSimplified[] getArtists();
    ExternalUrl getExternalUrls();
    int getTrackNumber();
    ModelObjectType getType();

}
