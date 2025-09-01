package com.github.musicsnsproject.repository.mongo.chat;

import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.*;

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
    private String chatRoomId; // MongoDB ObjectId

    // JPA User의 PK 저장 (예: Long userId)
    private List<Long> participants; // [userA_id, userB_id]
    private Set<Long> hiddenUserIds;
    private Map<Long, LocalDateTime> hiddenUserMap; // 유저별 나간 시점

    private LocalDateTime createdAt;

    public static ChatRoom create(List<Long> participants) {
        ChatRoom chatRoom = new ChatRoom();
        chatRoom.participants = participants;
        chatRoom.createdAt = LocalDateTime.now();
        chatRoom.hiddenUserIds = new HashSet<>();
        chatRoom.hiddenUserMap = new HashMap<>();
        return chatRoom;
    }
    public void exitRoom(long userId){
        hiddenUserIds.add(userId);
        hiddenUserMap.put(userId, LocalDateTime.now());
    }
    public boolean isUserInRoom(long userId){
        return participants.contains(userId);
    }
    public boolean isHiddenForUser(long userId){
        return hiddenUserIds.contains(userId);
    }

}
