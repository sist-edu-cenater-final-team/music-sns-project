package com.github.musicsnsproject.web.dto.chat;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ChatMessageResponse {
    private ChatUserInfo sender;
    private String content;
    private LocalDateTime sentAt;
}
