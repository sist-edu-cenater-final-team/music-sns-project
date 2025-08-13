package com.github.musicsnsproject.common.converter.custom;

import com.github.musicsnsproject.common.myenum.MusicSearchType;
import org.springframework.stereotype.Component;

@Component
public class MusicSearchTypeConverter extends MyConverter<MusicSearchType> {
    public MusicSearchTypeConverter() {
        super(MusicSearchType.class);
    }
}
