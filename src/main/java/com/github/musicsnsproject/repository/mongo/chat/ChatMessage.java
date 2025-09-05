package com.github.musicsnsproject.repository.mongo.chat;

import com.github.musicsnsproject.web.dto.chat.ChatMessageRequest;
import com.github.musicsnsproject.web.dto.chat.ChatMessageResponse;
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
    private List<Long> readBy;// 읽은 유저 ID 목록
    private LocalDateTime sentAt;


    public static ChatMessage create(ChatMessageRequest request, long senderId, Set<Long> otherActiveUserIds) {
        return createChatMessage(
                request.getChatRoomId(),
                request.getContent(),
                senderId,
                otherActiveUserIds
        );
    }

    public static ChatMessage create(ChatMessageResponse messageResponse, long senderId, Set<Long> otherActiveUserIds) {
        return createChatMessage(
                messageResponse.getChatRoomId(),
                messageResponse.getContent(),
                senderId,
                otherActiveUserIds
        );
    }


    private static ChatMessage createChatMessage(String chatRoomId, String content, long senderId, Set<Long> otherActiveUserIds) {
        ChatMessage chatMessage = new ChatMessage();

        chatMessage.chatRoomId = chatRoomId;
        chatMessage.userId = senderId;
        chatMessage.content = content;
        chatMessage.sentAt = LocalDateTime.now();
        chatMessage.readBy = new ArrayList<>();
        chatMessage.readBy.add(senderId); // 발신자는 자동으로 읽음 처리
        chatMessage.readBy.addAll(otherActiveUserIds);
        return chatMessage;
    }
    public void addReadBy(Long userId) {
        readBy.add(userId);
    }
}
