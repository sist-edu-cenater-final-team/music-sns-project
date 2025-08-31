package com.github.musicsnsproject.web.dto.chat;

import java.util.List;

public record ChatRoomResponse(String chatRoomId, ChatUserInfo loginUser, List<ChatUserInfo> otherUsers,
                               List<ChatMessageResponse> messages) {

}
