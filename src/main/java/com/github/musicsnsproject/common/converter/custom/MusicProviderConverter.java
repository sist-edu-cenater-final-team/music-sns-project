package com.github.musicsnsproject.common.converter.custom;

import com.github.musicsnsproject.common.myenum.MusicProvider;
import org.springframework.stereotype.Component;

@Component
public class MusicProviderConverter extends MyConverter<MusicProvider>{
    public MusicProviderConverter() {
        super(MusicProvider.class);
    }
}
