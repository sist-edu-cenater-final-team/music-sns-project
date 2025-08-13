package com.github.musicsnsproject.common.converter.custom;

import com.github.musicsnsproject.common.myenum.Gender;
import org.springframework.stereotype.Component;
@Component
public class GenderConverter extends MyConverter<Gender> {
    public GenderConverter(){
        super(Gender.class);
    }
}
