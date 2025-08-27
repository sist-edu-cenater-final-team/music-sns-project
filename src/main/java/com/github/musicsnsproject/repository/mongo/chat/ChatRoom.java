package com.github.musicsnsproject.repository.mongo.chat;

import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

/*
*
name = "participants_idx": 인덱스 이름 지정
def = "{'participants': 1}": participants 필드에 오름차순 인덱스 생성
unique = true: 동일한 participants 조합의 채팅방 중복 방지
* */
@Getter
@Document(collection = "chat_rooms")
@CompoundIndexes({
        @CompoundIndex(name = "participants_idx", def = "{'participants': 1}", unique = true)
})
public class ChatRoom {
    @Id
    private String id; // MongoDB ObjectId

    // JPA User의 PK 저장 (예: Long userId)
    private List<Long> participants; // [userA_id, userB_id]

    private LocalDateTime createdAt;

    public static ChatRoom create(List<Long> participants) {
        ChatRoom chatRoom = new ChatRoom();
        chatRoom.participants = participants;
        chatRoom.createdAt = LocalDateTime.now();
        return chatRoom;
    }
}
