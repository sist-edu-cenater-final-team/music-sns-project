package com.github.musicsnsproject.repository.mongo.chat;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface ChatRoomRepository extends MongoRepository<ChatRoom, String> {
    // 두 유저가 같은 participants 조합으로 존재하는 방 조회
    Optional<ChatRoom> findByParticipantsContainsAndParticipantsContains(Long userA, Long userB);

}
