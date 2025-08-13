package com.github.musicsnsproject.common.myenum;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MusicSearchType implements MyEnumInterface{
    ALL("전체", ""),
    TRACK("제목", "track:"),
    ARTIST("아티스트", "artist:"),
    ALBUM("앨범", "album:");
    private final String value;
    private final String prefix;
}
