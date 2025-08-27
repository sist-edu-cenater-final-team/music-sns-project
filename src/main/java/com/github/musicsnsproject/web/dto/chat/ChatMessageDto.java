package com.github.musicsnsproject.web.dto.chat;

import lombok.Getter;

@Getter
public class ChatMessageDto {
    private String chatRoomId;
    private Long senderId;
    private String content;
}
