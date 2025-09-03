package com.github.musicsnsproject.web.dto.chat;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.github.musicsnsproject.repository.mongo.chat.ChatMessage;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ChatMessageResponse {
    private final String chatRoomId;
    private final String chatMessageId;
    private final ChatUserInfo sender;
    private final String content;
    private final LocalDateTime sentAt;
    private final boolean isOldUnread;

    @JsonIgnore
    private final List<Long> participantIds;

    @JsonIgnore
    private final List<Long> readBy;

    public long getUnreadCount() {
        return participantIds.size() - readBy.size();
    }

    public static ChatMessageResponse of(ChatMessage chatMessage, ChatUserInfo sender, List<Long> chatTotalUserIds, boolean isOldUnread) {
        return new ChatMessageResponse(
                chatMessage.getChatRoomId(),
                chatMessage.getChatMessageId(),
                sender,
                chatMessage.getContent(),
                chatMessage.getSentAt(),
                isOldUnread,
                chatTotalUserIds,
                chatMessage.getReadBy()
        );
    }

    public static ChatMessageResponse of(ChatMessage chatMessage, ChatUserInfo sender, List<Long> chatTotalUserIds) {
        return new ChatMessageResponse(
                chatMessage.getChatRoomId(),
                chatMessage.getChatMessageId(),
                sender,
                chatMessage.getContent(),
                chatMessage.getSentAt(),
                false,
                chatTotalUserIds,
                chatMessage.getReadBy()
        );
    }
}