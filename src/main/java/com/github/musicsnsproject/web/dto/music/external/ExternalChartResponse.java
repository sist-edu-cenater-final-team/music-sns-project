package com.github.musicsnsproject.web.dto.music.external;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

@Builder
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ExternalChartResponse {
    private long rank;
    private String rankStatus; //up, static, down
    private long changedRank;
    private String artistName;
    private String title;
    private String albumName;
    private String albumArt;
    private String songNumber;
}
