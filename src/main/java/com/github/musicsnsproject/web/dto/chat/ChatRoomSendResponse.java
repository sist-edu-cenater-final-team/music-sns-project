package com.github.musicsnsproject.web.dto.chat;

import com.github.musicsnsproject.web.dto.chat.room.ReceiversInfo;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor(staticName = "of")
public class ChatRoomSendResponse {
    private String chatRoomId;
    private String lastMessage;
    private LocalDateTime sentAt;
    List<ReceiversInfo> receivers;
}
