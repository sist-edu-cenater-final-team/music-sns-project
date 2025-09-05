package com.github.musicsnsproject.web.dto.chat;

import lombok.Getter;

import java.util.Set;

@Getter
public class ChatToastResponse {
    private String chatRoomId;
    private String chatMessageId;
    private String content;
    private ChatUserInfo sender;
    private Set<Long> otherActiveUserIds;

    public static ChatToastResponse fromChatMessageResponse(ChatMessageResponse chatMessageResponse) {
        ChatToastResponse response = new ChatToastResponse();
        response.chatRoomId = chatMessageResponse.getChatRoomId();
        response.chatMessageId = chatMessageResponse.getChatMessageId();
        response.content = chatMessageResponse.getContent();
        response.sender = chatMessageResponse.getSender();
        response.otherActiveUserIds = chatMessageResponse.getOtherActiveUserIds();
        return response;
    }
}
