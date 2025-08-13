package com.github.musicsnsproject.common.myenum;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MyMusicType implements MyEnumInterface {
    PURCHASE("구매"),
    PRESENT("선물");
    private final String value;
}
