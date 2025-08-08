package com.github.musicsnsproject.common.myenum;

import lombok.AllArgsConstructor;
import lombok.Getter;
@Getter
@AllArgsConstructor
public enum GiftStatus implements MyEnumInterface {
    READY("대기"),
    ACCEPTED("승낙"),
    REJECTED("거부");
    private final String value;
}
