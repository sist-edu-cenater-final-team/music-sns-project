package com.github.musicsnsproject.web.dto.profile;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import se.michaelthelin.spotify.model_objects.specification.Artist;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProfileMusicResponse {
    private String musicId;
    private String musicName;
    private String artistName;
    private String albumImageUrl;

    private int listOrder;
}
