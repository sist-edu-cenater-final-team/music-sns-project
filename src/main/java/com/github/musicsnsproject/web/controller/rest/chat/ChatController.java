package com.github.musicsnsproject.web.controller.rest.chat;

import com.github.musicsnsproject.repository.mongo.chat.ChatMessage;
import com.github.musicsnsproject.repository.mongo.chat.ChatRoom;
import com.github.musicsnsproject.service.chat.ChatService;
import com.github.musicsnsproject.web.dto.chat.*;
import com.github.musicsnsproject.web.dto.response.CustomSuccessResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.user.SimpUser;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {
    private final ChatService chatService;
    private final SimpMessagingTemplate messagingTemplate;
    private final SimpUserRegistry userRegistry;

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
        CustomSuccessResponse<List<String>> response = CustomSuccessResponse.ofOk("채팅방에 누군가 들어왔어", messages.unreadMessageIds());
        messagingTemplate.convertAndSend("/chat/"+roomId, response);
        return CustomSuccessResponse.ofOk("채팅방 메세지 조회 성공", messages);
    }



    @PostMapping("/room")
    public CustomSuccessResponse<ChatRoom> getOrCreateRoom(
            @RequestParam Long targetUserId,
            @AuthenticationPrincipal Long userId
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
    private Set<Long> getActiveUsersInRoom(String roomId) {
        return userRegistry.getUsers().stream()
                .filter(user -> user.getSessions().stream()
                        .anyMatch(session -> session.getSubscriptions().stream()
                                .anyMatch(sub -> sub.getDestination().equals("/chat/" + roomId))))
                .map(simpUser -> Long.parseLong(simpUser.getName()))
                .collect(Collectors.toSet());
    }
    @GetMapping("/active-test")
    public String activeTest(@RequestParam String roomId){
        Set<Long> activeUsers = getActiveUsersInRoom(roomId);
        return "Active users in room " + roomId + ": " + activeUsers;
    }

    // 메시지 보내기
    @PostMapping("/message")
    public CustomSuccessResponse<Void> sendMessage(
            @RequestBody ChatMessageRequest request,
            @AuthenticationPrincipal Long senderId
    ) {
        // 현재 채팅방에 구독중인 사용자 확인
        Set<Long> activeUserIds = getActiveUsersInRoom(request.getChatRoomId());
        ChatMessageResponse saved = chatService.saveMessage(request, senderId, activeUserIds);
        // 방에 구독 중인 사용자에게 실시간 push
        messagingTemplate.convertAndSend("/chat/" + saved.getChatRoomId(),
                CustomSuccessResponse.of(HttpStatus.CREATED,"신규 메세지", saved));

        ChatRoomSendResponse roomSendInfos = chatService.getSendRoomMessage(saved);
        broadcastNewRoomMessage(roomSendInfos);

        return CustomSuccessResponse.emptyDataOk("메시지 전송 성공");
    }


    @DeleteMapping("/room")
    public CustomSuccessResponse<Void> exitRoom(@RequestParam String roomId,
                                                @AuthenticationPrincipal Long userId) {
        chatService.exitRoom(roomId, userId);
        return CustomSuccessResponse.emptyDataOk("채팅방 나가기 성공");
    }

}
