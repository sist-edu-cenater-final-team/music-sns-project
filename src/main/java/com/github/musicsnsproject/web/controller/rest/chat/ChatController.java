package com.github.musicsnsproject.web.controller.rest.chat;

import com.github.musicsnsproject.repository.mongo.chat.ChatMessage;
import com.github.musicsnsproject.repository.mongo.chat.ChatRoom;
import com.github.musicsnsproject.service.chat.ChatService;
import com.github.musicsnsproject.web.dto.chat.*;
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
    //나의 채팅목록 조회
    @GetMapping("/my-rooms")
    public CustomSuccessResponse<List<ChatRoomListResponse>> getMyChatRooms(@AuthenticationPrincipal Long userId){
        List<ChatRoomListResponse> roomResponses = chatService.getMyChatRoomList(userId);
        return CustomSuccessResponse.ofOk("나의 채팅방 목록 조회 성공", roomResponses);
    }
    @GetMapping("/{roomId}")
    public CustomSuccessResponse<ChatRoomResponse> getRoomMessages(
            @PathVariable String roomId,
            @AuthenticationPrincipal Long userId
    ) {
        ChatRoomResponse messages = chatService.getRoomMessages(roomId, userId);
        return CustomSuccessResponse.ofOk("채팅방 메세지 조회 성공", messages);
    }



    // 채팅방 생성 or 조회
    @PostMapping("/room")
    public CustomSuccessResponse<ChatRoom> getOrCreateRoom(
            @RequestParam Long targetUserId,
            @AuthenticationPrincipal Long userId // JWT 인증된 유저
    ) {
        ChatRoom room = chatService.getOrCreateChatRoom(userId, targetUserId);
        return CustomSuccessResponse.ofOk("채팅방 조회 성공", room);
    }

    private void broadcastNewRoomMessage(ChatRoomSendResponse roomSendInfos){
        roomSendInfos.getReceivers().forEach(receiver -> {
            var response = ChatRoomListResponse.fromSendResponse(roomSendInfos, receiver);
            messagingTemplate.convertAndSend("/rooms/"+receiver.getReceiverId(), CustomSuccessResponse.ofOk("신규 메세지 반영 채팅방", response));
        });
    }

    // 메시지 보내기
    @PostMapping("/message")
    public CustomSuccessResponse<Void> sendMessage(
            @RequestBody ChatMessageRequest request,
            @AuthenticationPrincipal Long senderId
    ) {
        ChatMessageResponse saved = chatService.saveMessage(request, senderId);
        // 방에 구독 중인 사용자에게 실시간 push
//        messagingTemplate.convertAndSend("/topic/" + saved.getChatRoomId(), saved);
        ChatRoomSendResponse roomSendInfos = chatService.getSendRoomMessage(request.getChatRoomId());
        broadcastNewRoomMessage(roomSendInfos);

        messagingTemplate.convertAndSend(
                "/chat/"+request.getChatRoomId(),
                CustomSuccessResponse.emptyDataOk("메롱이다")
        );

        return CustomSuccessResponse.emptyDataOk("메시지 전송 성공");
    }

    // 대화내역 조회
    @GetMapping("/messages/{roomId}")
    public CustomSuccessResponse<List<ChatMessage>> getMessages(@PathVariable String roomId) {
        return CustomSuccessResponse.ofOk("메세지 불러오기 성공", chatService.getMessages(roomId));
    }
    @DeleteMapping("/room")
    public CustomSuccessResponse<Void> exitRoom(@RequestParam String roomId,
                                                @AuthenticationPrincipal Long userId) {
        chatService.exitRoom(roomId, userId);
        return CustomSuccessResponse.emptyDataOk("채팅방 나가기 성공");
    }

}
