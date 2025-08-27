package com.github.musicsnsproject.web.controller.rest.chat;

import com.github.musicsnsproject.repository.mongo.chat.ChatMessage;
import com.github.musicsnsproject.repository.mongo.chat.ChatRoom;
import com.github.musicsnsproject.service.chat.ChatService;
import com.github.musicsnsproject.web.dto.chat.ChatMessageRequest;
import com.github.musicsnsproject.web.dto.response.CustomSuccessResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {
    private final ChatService chatService;
    private final SimpMessagingTemplate messagingTemplate;

    // 채팅방 생성 or 조회
    @PostMapping("/room")
    public CustomSuccessResponse<ChatRoom> getOrCreateRoom(
            @RequestParam Long targetUserId,
            @AuthenticationPrincipal Long userId // JWT 인증된 유저
    ) {
        ChatRoom room = chatService.getOrCreateChatRoom(userId, targetUserId);
        return CustomSuccessResponse.ofOk("채팅방 조회 성공", room);
    }

    // 메시지 보내기
    @PostMapping("/message")
    public CustomSuccessResponse<ChatMessage> sendMessage(
            @RequestBody ChatMessageRequest request,
            @AuthenticationPrincipal Long senderId
    ) {
        ChatMessage saved = chatService.sendMessage(request.getChatRoomId(), senderId, request.getContent());

        // 방에 구독 중인 사용자에게 실시간 push
        messagingTemplate.convertAndSend("/topic/" + saved.getChatRoomId(), saved);

        return CustomSuccessResponse.ofOk("메시지 전송 성공", saved);
    }

    // 대화내역 조회
    @GetMapping("/messages/{roomId}")
    public CustomSuccessResponse<List<ChatMessage>> getMessages(@PathVariable String roomId) {
        return CustomSuccessResponse.ofOk("메세지 불러오기 성공", chatService.getMessages(roomId));
    }

}
