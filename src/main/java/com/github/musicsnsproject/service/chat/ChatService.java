package com.github.musicsnsproject.service.chat;

import com.github.musicsnsproject.repository.mongo.chat.ChatMessage;
import com.github.musicsnsproject.repository.mongo.chat.ChatMessageRepository;
import com.github.musicsnsproject.repository.mongo.chat.ChatRoom;
import com.github.musicsnsproject.repository.mongo.chat.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatService {
    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;

    /**
     * 두 유저의 채팅방 조회 or 새로 생성
     */
    public ChatRoom getOrCreateChatRoom(Long userAId, Long userBId) {
        return chatRoomRepository.findByParticipantsContainsAndParticipantsContains(userAId, userBId)
                .orElseGet(() -> {
                    ChatRoom room = ChatRoom.create(List.of(userAId, userBId));
                    return chatRoomRepository.save(room);
                });
    }


    /**
     * 메시지 저장
     */
    public ChatMessage sendMessage(String chatRoomId, Long senderId, String content) {
        ChatMessage message = ChatMessage.create(chatRoomId, senderId, content);
        return chatMessageRepository.save(message);
    }

    /**
     * 대화 내역 조회
     */
    public List<ChatMessage> getMessages(String chatRoomId) {
        return chatMessageRepository.findByChatRoomIdOrderBySentAtAsc(chatRoomId);
    }
}
