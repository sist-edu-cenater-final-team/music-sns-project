package com.github.musicsnsproject.service.chat;

import com.github.musicsnsproject.common.exceptions.CustomNotFoundException;
import com.github.musicsnsproject.common.myenum.UserStatus;
import com.github.musicsnsproject.repository.jpa.account.user.MyUserRepository;
import com.github.musicsnsproject.repository.mongo.chat.ChatMessage;
import com.github.musicsnsproject.repository.mongo.chat.ChatMessageRepository;
import com.github.musicsnsproject.repository.mongo.chat.ChatRoom;
import com.github.musicsnsproject.repository.mongo.chat.ChatRoomRepository;
import com.github.musicsnsproject.web.dto.chat.ChatRoomListResponse;
import com.github.musicsnsproject.web.dto.chat.ChatUserInfo;
import com.github.musicsnsproject.web.dto.chat.logic.RoomEntityCollection;
import com.github.musicsnsproject.web.dto.chat.logic.UnreadCount;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class ChatService {
    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final MyUserRepository myUserRepository;
    private final MongoTemplate mongoTemplate;

    /**
     * 두 유저의 채팅방 조회 or 새로 생성
     */
    public ChatRoom getOrCreateChatRoom(Long loginUserId, Long targetUserId) {
        List<UserStatus> activeStatuses = List.of(UserStatus.NORMAL, UserStatus.LOCK);
        boolean isUser = myUserRepository.existsByUserId_AndStatusIn(targetUserId, activeStatuses);
        if (!isUser)
            throw CustomNotFoundException.of().request(targetUserId).customMessage("존재 하지 않는 유저이거나 비활성 계정").build();
        List<Long> participants = Stream.of(loginUserId, targetUserId)
                .sorted().toList();

        ChatRoom chatRoom = chatRoomRepository.findByParticipants(participants)
                .orElseGet(() -> {
                    ChatRoom room = ChatRoom.create(participants);
                    return chatRoomRepository.save(room);
                });

        return chatRoomRepository.findByParticipants(participants)
                .orElseGet(() -> {
                    ChatRoom room = ChatRoom.create(participants);
                    return chatRoomRepository.save(room);
                });
    }


    /**
     * 메시지 저장
     */
    public ChatMessage sendMessage(String chatRoomId, Long senderId, String content) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> CustomNotFoundException.of().request(chatRoomId).customMessage("존재하지 않는 채팅방").build());
        Set<Long> otherIds = chatRoom.getParticipants().stream()
                .filter(id -> !id.equals(senderId))
                .collect(Collectors.toUnmodifiableSet());
        List<ChatUserInfo> otherUsers = myUserRepository.findAllByIdForChatRoom(otherIds);




        ChatMessage message = ChatMessage.create(chatRoomId, senderId, content, otherUsers.size());
        chatMessageRepository.save(message);
        return chatMessageRepository.save(message);
    }

    /**
     * 대화 내역 조회
     */
    public List<ChatMessage> getMessages(String chatRoomId) {
        return chatMessageRepository.findByChatRoomIdOrderBySentAtAsc(chatRoomId);
    }


    private RoomEntityCollection createEntityCollection(Long loginUserId) {
        List<ChatRoom> chatRooms = chatRoomRepository.findByParticipantsContains(loginUserId);
        RoomEntityCollection roomEntityCollection = new RoomEntityCollection(chatRooms);
        Set<Long> allOtherIds = chatRoomToOtherIdList(chatRooms, loginUserId);
        Set<String> allRoomIds = roomEntityCollection.getChatRoomMap().keySet();

        List<ChatUserInfo> userInfos = myUserRepository
                .findAllByIdForChatRoom(allOtherIds);
        List<UnreadCount> allUnreadCounts = chatMessageRepository
                .countUnreadByChatRoomIds(allRoomIds, loginUserId);
        List<ChatMessage> allLastMessages = getLastChatMessageByRoomIds(allRoomIds);

        roomEntityCollection.setField(userInfos, allUnreadCounts, allLastMessages, loginUserId);
        return roomEntityCollection;
    }
    private List<ChatMessage> getLastChatMessageByRoomIds(Set<String> allRoomIds) {
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(Criteria.where("chatRoomId").in(allRoomIds)), // roomId 조건
                Aggregation.sort(Sort.Direction.DESC, "sentAt"),               // 최신순 정렬
                Aggregation.group("chatRoomId").first(Aggregation.ROOT).as("lastMessage"), // 각 그룹의 첫 번째(=최신)만 가져옴
                Aggregation.replaceRoot("lastMessage") // lastMessage 필드를 최상위로 변환
        );
        return mongoTemplate.aggregate(
                aggregation,
                "chat_messages", // 컬렉션 이름
                ChatMessage.class
        ).getMappedResults();
    }

    @Transactional(readOnly = true)
    public List<ChatRoomListResponse> getMyChatRoomList(Long loginUserId) {
        RoomEntityCollection roomEntityCollection = createEntityCollection(loginUserId);

        List<ChatRoomListResponse> responses = createRoomListResponses(roomEntityCollection);
        // 최근 메세지 순으로 정렬
        responses.sort((r1, r2) ->
                r2.getLastMessageTime().compareTo(r1.getLastMessageTime()));
        return responses;
    }

    private List<ChatRoomListResponse> createRoomListResponses(RoomEntityCollection entityCollection) {
        return entityCollection.getChatMessages().stream()
                .map(lastMessage -> {
                    String roomId = lastMessage.getChatRoomId();
                    return ChatRoomListResponse.of(
                            lastMessage,
                            entityCollection.getOtherUserInfoListMap().get(roomId),
                            entityCollection.getUnreadCountMap().get(roomId) != null ?
                                    entityCollection.getUnreadCountMap().get(roomId).getUnreadCount() : 0L
                    );
                }).collect(Collectors.toList());
    }


    private Set<Long> chatRoomToOtherIdList(List<ChatRoom> chatRooms, Long userId) {
        return chatRooms.stream()
                .flatMap(chatRoom -> chatRoom.getParticipants().stream()
                        .filter(id -> !id.equals(userId)))
                .distinct()
                .collect(Collectors.toUnmodifiableSet());
    }



    private <T, R> Map<T, R> listToKeyMap(Function<R, T> keyMapper, List<R> list) {
        return list.stream()
                .collect(Collectors.toMap(keyMapper, item -> item));
    }


    /**
     * 사용 참고
     * List<Long> test = flatMapList(
     * chatRoom -> chatRoom.getParticipants().stream()
     * .filter(id -> !id.equals(loginUerId)),
     * chatRooms
     * );
     * List<String> test2 = flatMapList(
     * chatRoom -> Stream.of(chatRoom.getChatRoomId()),
     * chatRooms
     * );
     */
    private <R, T> List<T> flatMapList(Function<R, Stream<T>> mapper, List<R> list) {
        return list.stream()
                .flatMap(mapper)
                .distinct()
                .toList();
    }

}
