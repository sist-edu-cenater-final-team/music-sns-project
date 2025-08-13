package com.github.musicsnsproject.common.converter.custom;

import com.github.musicsnsproject.common.myenum.MyMusicType;
import org.springframework.stereotype.Component;

@Component
public class MyMusicTypeConverter extends MyConverter<MyMusicType>{
    public MyMusicTypeConverter() {
        super(MyMusicType.class);
    }
}
