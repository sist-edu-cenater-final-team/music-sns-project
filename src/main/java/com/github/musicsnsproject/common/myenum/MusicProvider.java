package com.github.musicsnsproject.common.myenum;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MusicProvider implements MyEnumInterface {
    BUGS("벅스"),
    VIBE("바이브"),
    MELON("멜론"),
    GENIE("지니"),
    SPOTIFY("스포티파이");
    private final String value;
}
