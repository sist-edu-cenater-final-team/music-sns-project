package com.github.musicsnsproject.web.dto.chat;

import java.util.List;

public class ChatRoomResponse {
    private String chatRoomId;
    private List<ChatMessageResponse> messages;
    private long loginUserId;
    private List<Long> otherUserIds;
}
