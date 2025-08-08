package com.github.musicsnsproject.common.converter.custom;

import com.github.musicsnsproject.common.myenum.GiftStatus;
import org.springframework.stereotype.Component;

@Component
public class GiftStatusConverter extends MyConverter<GiftStatus>{
    public GiftStatusConverter() {
        super(GiftStatus.class);
    }
}
