package com.github.musicsnsproject.web.dto.chat;

import lombok.Getter;

@Getter
public class ChatMessageRequest {
    private String chatRoomId;
    private String content;
}
