package com.github.musicsnsproject.web.dto.music.external;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
public class ExternalDetailResponse {
    private String title;
    private String number;

}
