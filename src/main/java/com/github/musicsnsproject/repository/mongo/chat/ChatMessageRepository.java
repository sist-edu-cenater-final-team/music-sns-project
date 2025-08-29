package com.github.musicsnsproject.repository.mongo.chat;

import com.github.musicsnsproject.web.dto.chat.logic.UnreadCount;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface ChatMessageRepository extends MongoRepository<ChatMessage, String> {
    List<ChatMessage> findByChatRoomIdOrderBySentAtAsc(String chatRoomId);



    /** 각 채팅방 별 내가 안읽은 메세지 카운트*/
    @Aggregation(pipeline = {
            "{ '$match': { 'chatRoomId': { '$in': ?0 }, 'userId': { '$ne': ?1 }, 'unreadCount': { '$gte': 1 }, 'readBy': { '$ne': ?1 } } }",
            "{ '$group': { '_id': '$chatRoomId', 'count': { '$sum': 1 } } }"
    })
    List<UnreadCount> countUnreadByChatRoomIds(Set<String> chatRoomIds, Long userId);
}
