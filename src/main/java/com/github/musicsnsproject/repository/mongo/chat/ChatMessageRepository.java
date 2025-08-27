package com.github.musicsnsproject.repository.mongo.chat;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ChatMessageRepository extends MongoRepository<ChatMessage, String> {
    List<ChatMessage> findByChatRoomIdOrderBySentAtAsc(String chatRoomId);

}
