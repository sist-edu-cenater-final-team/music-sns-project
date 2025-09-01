package com.github.musicsnsproject.web.dto.chat;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor(staticName = "of")
public class ChatMessageResponse {
    private ChatUserInfo sender;
    private String content;
    private LocalDateTime sentAt;
    private long unreadCount;
    private boolean isOldUnread;
}
