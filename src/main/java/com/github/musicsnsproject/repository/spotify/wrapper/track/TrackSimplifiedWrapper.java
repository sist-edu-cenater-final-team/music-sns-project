package com.github.musicsnsproject.repository.spotify.wrapper.track;

import lombok.AllArgsConstructor;
import se.michaelthelin.spotify.enums.ModelObjectType;
import se.michaelthelin.spotify.model_objects.specification.ArtistSimplified;
import se.michaelthelin.spotify.model_objects.specification.ExternalUrl;
import se.michaelthelin.spotify.model_objects.specification.TrackSimplified;

@AllArgsConstructor(staticName = "of")
public class TrackSimplifiedWrapper implements TrackBase {
    private final TrackSimplified track;

    @Override
    public String getId() {
        return track.getId();
    }

    @Override
    public String getName() {
        return track.getName();
    }

    @Override
    public int getDurationMs() {
        return track.getDurationMs();
    }

    @Override
    public ArtistSimplified[] getArtists() {
        return track.getArtists();
    }


    @Override
    public ExternalUrl getExternalUrls() {
        return track.getExternalUrls();
    }


    @Override
    public int getTrackNumber() {
        return track.getTrackNumber();
    }

    @Override
    public ModelObjectType getType() {
        return track.getType();
    }


}
