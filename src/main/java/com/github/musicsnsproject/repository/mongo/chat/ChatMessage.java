package com.github.musicsnsproject.repository.mongo.chat;

import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Document(collection = "chat_messages")
public class ChatMessage {
    @Id
    private String chatMessageId;
    private String chatRoomId;
    private Long userId;
    private String content;
    private long unreadCount;
    private List<Long> readBy;// 읽은 유저 ID 목록
    private LocalDateTime sentAt;

    public static ChatMessage create(String chatRoomId, long senderId, String content, int unreadCount) {
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.chatRoomId = chatRoomId;
        chatMessage.userId = senderId;
        chatMessage.readBy.add(senderId);
        chatMessage.content = content;
        chatMessage.unreadCount = unreadCount;
        chatMessage.sentAt = LocalDateTime.now();
        chatMessage.readBy = new ArrayList<>();
        return chatMessage;
    }

    public void addReadBy(Long userId) {
        readBy.add(userId);
        unreadCount = Math.max(0, unreadCount - 1);
    }
}
