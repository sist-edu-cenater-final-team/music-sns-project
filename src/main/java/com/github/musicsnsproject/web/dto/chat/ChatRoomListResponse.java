package com.github.musicsnsproject.web.dto.chat;

import com.github.musicsnsproject.repository.mongo.chat.ChatMessage;
import com.github.musicsnsproject.web.dto.chat.room.ReceiversInfo;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter

public class ChatRoomListResponse {
    private String chatRoomId;
    private List<ChatUserInfo> otherUsers;
    private String lastMessage;
    private LocalDateTime lastMessageTime;
    private long unreadCount;

    public static ChatRoomListResponse of(ChatMessage chatMessage, List<ChatUserInfo> otherUsers, long unreadCount) {
        ChatRoomListResponse chatRoomListResponse = new ChatRoomListResponse();
        chatRoomListResponse.chatRoomId = chatMessage.getChatRoomId();
        chatRoomListResponse.otherUsers = otherUsers;
        chatRoomListResponse.lastMessage = chatMessage.getContent();
        chatRoomListResponse.lastMessageTime = chatMessage.getSentAt();

        chatRoomListResponse.unreadCount = unreadCount;
        return chatRoomListResponse;
    }
    public static ChatRoomListResponse fromSendResponse(ChatRoomSendResponse sendResponse, ReceiversInfo receiversInfo) {
        ChatRoomListResponse chatRoomListResponse = new ChatRoomListResponse();
        chatRoomListResponse.chatRoomId = sendResponse.getChatRoomId();
        chatRoomListResponse.otherUsers = receiversInfo.getOtherUsers();
        chatRoomListResponse.lastMessage = sendResponse.getLastMessage();
        chatRoomListResponse.lastMessageTime = sendResponse.getSentAt();

        chatRoomListResponse.unreadCount = receiversInfo.getUnreadCount();
        return chatRoomListResponse;
    }

}
