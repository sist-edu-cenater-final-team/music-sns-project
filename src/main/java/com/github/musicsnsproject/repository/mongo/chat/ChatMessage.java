package com.github.musicsnsproject.repository.mongo.chat;

import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
@Getter
@Document(collection = "chat_messages")
public class ChatMessage {
    @Id
    private String messageId;
    private String chatRoomId;
    private Long senderId;
    private String content;
    private LocalDateTime sentAt;

    public static ChatMessage create(String chatRoomId, long senderId, String content){
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.chatRoomId = chatRoomId;
        chatMessage.senderId = senderId;
        chatMessage.content = content;
        chatMessage.sentAt = LocalDateTime.now();
        return chatMessage;
    }
}
