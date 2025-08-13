package com.github.musicsnsproject.common.converter.custom;

import com.github.musicsnsproject.common.myenum.EmotionEnum;
import org.springframework.stereotype.Component;

@Component
public class EmotionConverter extends MyConverter<EmotionEnum> {
    public EmotionConverter(){
        super(EmotionEnum.class);
    }
}
