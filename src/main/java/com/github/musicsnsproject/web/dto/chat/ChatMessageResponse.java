package com.github.musicsnsproject.web.dto.chat;

import com.github.musicsnsproject.repository.mongo.chat.ChatMessage;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

public record ChatMessageResponse(String chatMessageId, ChatUserInfo sender, String content, LocalDateTime sentAt,
                                  long unreadCount, boolean isOldUnread) {
    public ChatMessageResponse(ChatMessage message, ChatUserInfo sender){
        this(
                message.getChatMessageId(),
                sender,
                message.getContent(),
                message.getSentAt(),
                message.getUnreadCount(),
                false
        );
    }
}
