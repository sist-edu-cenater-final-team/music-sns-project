package com.github.musicsnsproject.web.dto.chat.logic;

import lombok.Getter;
import org.springframework.data.mongodb.core.mapping.Field;

@Getter
public class UnreadCount {
    @Field("_id")
    private String chatRoomId;
    @Field("count")
    private long unreadCount;
}
