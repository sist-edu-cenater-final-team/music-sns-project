package com.github.musicsnsproject.web.dto.chat.logic;

import com.github.musicsnsproject.repository.mongo.chat.ChatMessage;
import com.github.musicsnsproject.repository.mongo.chat.ChatRoom;
import com.github.musicsnsproject.web.dto.chat.ChatUserInfo;
import lombok.Getter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Getter
public class RoomEntityCollection {
    private final Map<String, ChatRoom> chatRoomMap;
    private final Map<String, List<ChatUserInfo>> otherUserInfoListMap;

    private Map<String, UnreadCount> unreadCountMap;
    private List<ChatMessage> chatMessages;

    public RoomEntityCollection(List<ChatRoom> chatRoomMap) {
        this.chatRoomMap = listToKeyMap(ChatRoom::getChatRoomId, chatRoomMap);
        this.otherUserInfoListMap = new HashMap<>();
    }

    public void setField(List<ChatUserInfo> userInfos, List<UnreadCount> unreadCounts, List<ChatMessage> chatMessages, Long loginUserId) {
        Map<Long, ChatUserInfo> otherUserInfoMap = listToKeyMap(ChatUserInfo::getUserId, userInfos);
        setOtherUserInfoListMap(otherUserInfoMap, loginUserId);
        this.unreadCountMap = listToKeyMap(UnreadCount::getChatRoomId, unreadCounts);
        this.chatMessages = chatMessages;
    }
    private void setOtherUserInfoListMap(Map<Long, ChatUserInfo> otherUserInfoMap, Long loginUserId) {
        this.chatRoomMap.forEach((roomId, chatRoom) -> {
            List<Long> otherUserIds = chatRoom.getParticipants().stream()
                    .filter(id -> !id.equals(loginUserId))
                    .toList();
            List<ChatUserInfo> otherUsers = otherUserIds.stream()
                    .map(otherUserInfoMap::get)
                    .toList();
            this.otherUserInfoListMap.put(roomId, otherUsers);
        });
    }



    private <T, R> Map<T, R> listToKeyMap(Function<R, T> keyMapper, List<R> list) {
        return list.stream()
                .collect(Collectors.toMap(keyMapper, item -> item));
    }
}
