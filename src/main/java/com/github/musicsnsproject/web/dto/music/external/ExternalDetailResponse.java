package com.github.musicsnsproject.web.dto.music.external;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ExternalDetailResponse {
    private String title;
    private String number;

}
