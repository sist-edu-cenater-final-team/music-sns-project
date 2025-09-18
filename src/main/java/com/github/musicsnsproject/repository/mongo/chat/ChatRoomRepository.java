package com.github.musicsnsproject.repository.mongo.chat;

import com.github.musicsnsproject.web.dto.chat.logic.UnreadCount;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface ChatRoomRepository extends MongoRepository<ChatRoom, String> {
    // 두 유저가 같은 participants 조합으로 존재하는 방 조회
    Optional<ChatRoom> findByParticipants(List<Long> participants);

    List<ChatRoom> findByParticipantsContains(Long userId);

    @Query("{ 'participants': ?0, 'hiddenUserIds': { $nin: [?0] } }")
    List<ChatRoom> findMyActiveRoomByUserId(Long userId);

}
