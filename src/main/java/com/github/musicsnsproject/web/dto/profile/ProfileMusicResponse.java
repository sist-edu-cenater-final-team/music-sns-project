package com.github.musicsnsproject.web.dto.profile;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProfileMusicResponse {
    private String musicId;
    private String musicName;
    private String albumImageUrl;
    private int listOrder;
}
