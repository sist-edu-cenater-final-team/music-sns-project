package com.github.musicsnsproject.repository.mongo.chat;

import com.github.musicsnsproject.web.dto.chat.ChatMessageRequest;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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

    public static ChatMessage create(ChatMessageRequest request, long senderId, long unreadCount, Set<Long> otherActiveUserIds) {

        ChatMessage chatMessage = new ChatMessage();
        chatMessage.chatRoomId = request.getChatRoomId();
        chatMessage.userId = senderId;
        chatMessage.content = request.getContent();
        chatMessage.unreadCount = unreadCount - otherActiveUserIds.size();
        chatMessage.sentAt = LocalDateTime.now();
        chatMessage.readBy = new ArrayList<>();
        chatMessage.readBy.addAll(otherActiveUserIds);
        return chatMessage;
    }

    public void addReadBy(Long userId) {
        readBy.add(userId);
        unreadCount = Math.max(0, unreadCount - 1);
    }
}
