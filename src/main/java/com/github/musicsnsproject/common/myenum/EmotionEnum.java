package com.github.musicsnsproject.common.myenum;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum EmotionEnum implements MyEnumInterface {

    CALM("평온"),
    SAD("우울"),
    LOVE("사랑"),
    HAPPY("기쁨"),
    TIRED("피곤"),
    ANGRY("화남");
    private final String value;
}
