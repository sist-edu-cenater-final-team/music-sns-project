package com.github.musicsnsproject.web.dto.chat.room;

import com.github.musicsnsproject.web.dto.chat.ChatUserInfo;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor(staticName = "of")
public class ReceiversInfo {
    private Long receiverId;
    private List<ChatUserInfo> otherUsers;
    private long unreadCount;
}
