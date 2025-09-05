package com.github.musicsnsproject.web.dto.chat;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.List;

public record ChatRoomResponse(
        String chatRoomId,
        ChatUserInfo loginUser,
        List<ChatUserInfo> otherUsers,
        List<ChatMessageResponse> messages,
        @JsonIgnore
        List<String> unreadMessageIds
) {
}
