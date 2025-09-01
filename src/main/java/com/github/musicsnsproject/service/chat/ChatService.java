package com.github.musicsnsproject.service.chat;

import com.github.musicsnsproject.common.exceptions.CustomNotFoundException;
import com.github.musicsnsproject.common.myenum.UserStatus;
import com.github.musicsnsproject.repository.jpa.account.user.MyUserRepository;
import com.github.musicsnsproject.repository.mongo.chat.ChatMessage;
import com.github.musicsnsproject.repository.mongo.chat.ChatMessageRepository;
import com.github.musicsnsproject.repository.mongo.chat.ChatRoom;
import com.github.musicsnsproject.repository.mongo.chat.ChatRoomRepository;
import com.github.musicsnsproject.web.dto.chat.*;
import com.github.musicsnsproject.web.dto.chat.logic.RoomEntityCollection;
import com.github.musicsnsproject.web.dto.chat.logic.UnreadCount;
import com.github.musicsnsproject.web.dto.chat.room.ReceiversInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
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

        return chatRoomRepository.findByParticipants(participants)
                .orElseGet(() -> {
                    ChatRoom room = ChatRoom.create(participants);
                    return chatRoomRepository.save(room);
                });
    }


    /**
     * 메시지 저장
     */
    public ChatMessageResponse saveMessage(ChatMessageRequest chatMessageRequest, Long senderId) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatMessageRequest.getChatRoomId())
                .orElseThrow(() -> CustomNotFoundException.of().request(chatMessageRequest).customMessage("존재하지 않는 채팅방").build());

        Set<Long> otherIds = chatRoom.getParticipants().stream()
                .filter(id -> !id.equals(senderId))
                .collect(Collectors.toUnmodifiableSet());
//        Set<Long> test = listToSet(id->id.equals(senderId), chatRoom.getParticipants(), senderId);
        ChatMessage message = ChatMessage.create(chatMessageRequest, senderId, otherIds.size());
        ChatMessage saved = chatMessageRepository.save(message);
        ChatUserInfo sender = myUserRepository.findAllByIdForChatRoom(Set.of(senderId)).get(0);

        return new ChatMessageResponse(saved, sender);
    }


    /**
     * 대화 내역 조회
     */
    public List<ChatMessage> getMessages(String chatRoomId) {
        return chatMessageRepository.findByChatRoomIdOrderBySentAtAsc(chatRoomId);
    }


    private RoomEntityCollection createEntityCollection(Long loginUserId) {
//        List<ChatRoom> test = chatRoomRepository.findByParticipantsContains(loginUserId);
        List<ChatRoom> chatRooms = chatRoomRepository.findMyActiveRoomByUserId(loginUserId);
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
//    private <T, R> Set<T> listToSet(Function<R, T> filter, List<R> list, T exclude){
//        return list.stream()
//                .filter(mapper)
//                .collect(Collectors.toUnmodifiableSet());
//    }


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

    /** 채팅방 나가기 */
    public void exitRoom(String roomId, Long userId) {
        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> CustomNotFoundException.of().request(roomId).customMessage("존재하지 않는 채팅방").build());
        if (!chatRoom.isUserInRoom(userId))
            throw CustomNotFoundException.of().request(userId).customMessage("채팅방에 참여중이지 않은 유저").build();
        if (chatRoom.isHiddenForUser(userId))
            throw CustomNotFoundException.of().request(userId).customMessage("이미 채팅방에서 나간 유저").build();
        chatRoom.exitRoom(userId);
        chatRoomRepository.save(chatRoom);
    }
    /** 나를 제외한 참여중인 유저 아이디*/
    public List<Long> otherIdsInRoom(String chatRoomId, Long senderId) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> CustomNotFoundException.of().request(chatRoomId).customMessage("존재하지 않는 채팅방").build());
        return chatRoom.getParticipants().stream()
                .filter(id -> !id.equals(senderId))
                .toList();
    }

    /** 채팅방 메세지 전송 후 채팅방리스트에 반환용 */
    public ChatRoomSendResponse getSendRoomMessage(String chatRoomId ) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> CustomNotFoundException.of().request(chatRoomId).customMessage("존재하지 않는 채팅방").build());
        Set<Long> allUserIds = new HashSet<>(chatRoom.getParticipants());
        List<ChatUserInfo> allUserInfos = myUserRepository
                .findAllByIdForChatRoom(allUserIds);
        List<ReceiversInfo> receivers = crateReceiversInfo(allUserInfos, chatRoomId);
        ChatMessage lastMessages = getLastChatMessageByRoomIds(Set.of(chatRoomId)).get(0);

        return ChatRoomSendResponse.of(chatRoomId, lastMessages.getContent(), lastMessages.getSentAt(), receivers);
    }

    /** 모든 유저들의 정보를 각각 수신자와 발신자로 한번씩 정리  */
    private List<ReceiversInfo> crateReceiversInfo(List<ChatUserInfo> allUserInfos, String chatRoomId) {
        List<ReceiversInfo> receivers = new ArrayList<>(allUserInfos.size());
        for (ChatUserInfo userInfo : allUserInfos) {
            List<ChatUserInfo> otherInfos = allUserInfos.stream()
                    .filter(info -> info.getUserId() != userInfo.getUserId() )
                    .toList();
            UnreadCount unreadCount = chatMessageRepository
                    .countUnreadByChatRoomIds(Set.of(chatRoomId), userInfo.getUserId())
                    .stream().findFirst().orElse(null);
            ReceiversInfo info = ReceiversInfo.of(
                    userInfo.getUserId(),
                    otherInfos,
                    unreadCount != null ? unreadCount.getUnreadCount() : 0L
            );
            receivers.add(info);
        }
        return receivers;
    }
    private void validationRoomAndUser(ChatRoom chatRoom, long userId){
        if( !chatRoom.isUserInRoom(userId) )
            throw CustomNotFoundException.of().request(userId).customMessage("채팅방에 참여중이지 않은 유저").build();
        if( chatRoom.isHiddenForUser(userId) )
            throw CustomNotFoundException.of().request(userId).customMessage("채팅방에서 나간 유저").build();
    }


    @Transactional(readOnly = true)
    public ChatRoomResponse getRoomMessages(String roomId, long userId) {
        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> CustomNotFoundException.of().request(roomId).customMessage("존재하지 않는 채팅방").build());
        validationRoomAndUser(chatRoom, userId);
        List<ChatMessage> messages = chatMessageRepository.findByChatRoomIdOrderBySentAtAsc(roomId);
        if(messages.isEmpty()) return null;

        // 내가 안읽은 메세지들 중 내가 읽은걸로 변경
        List<ChatMessage> unreadMessages = getMyUnreadMessages(messages, userId);
        String oldUnreadId = changeUnreadMessagesToRead(unreadMessages, userId);
        // 참여중인 유저 정보들
        List<ChatUserInfo> participants = myUserRepository.findAllByIdForChatRoom(new HashSet<>(chatRoom.getParticipants()));
        Map<Long, ChatUserInfo> userInfoMap = listToKeyMap(ChatUserInfo::getUserId, participants);
        // 응답용 메세지 리스트
        List<ChatMessageResponse> chatMessageResponses = chatMessageListToResponseList(messages, userInfoMap, oldUnreadId);
        return createRoomResponse(userInfoMap, userId, roomId, chatMessageResponses);

    }

    private List<ChatMessageResponse> chatMessageListToResponseList(List<ChatMessage> messages, Map<Long, ChatUserInfo> userInfoMap, String oldUnreadId) {
        List<ChatMessageResponse> responses = new ArrayList<>(messages.size());
        for (ChatMessage message : messages) {
            ChatUserInfo senderInfo = userInfoMap.get(message.getUserId());
            boolean oldUnread = message.getChatMessageId().equals(oldUnreadId);
            ChatMessageResponse response = new ChatMessageResponse(
                    message.getChatMessageId(),
                    senderInfo, message.getContent(),
                    message.getSentAt(),
                    message.getUnreadCount(),
                    oldUnread
            );
            responses.add(response);
        }
        return responses;
    }

    private ChatRoomResponse createRoomResponse(Map<Long,ChatUserInfo> userInfoMap, long userId, String roomId, List<ChatMessageResponse> messageResponse) {
        ChatUserInfo me = userInfoMap.get(userId);
        List<ChatUserInfo> otherUsers = userInfoMap.values().stream()
                .filter(userInfo -> userInfo.getUserId() != userId )
                .toList();
        return new ChatRoomResponse(roomId, me, otherUsers, messageResponse);
    }

    private List<ChatUserInfo> getOtherUsersInfo(List<ChatUserInfo> participants, long userId) {
        return participants.stream()
                .filter(userInfo -> userInfo.getUserId() != userId )
                .toList();
    }
    private ChatUserInfo getMeInfo(List<ChatUserInfo> participants, long userId) {
        return participants.stream()
                .filter(userInfo -> userInfo.getUserId() == userId )
                .findFirst()
                .orElseThrow(() -> CustomNotFoundException.of().request(userId).customMessage("채팅방에 참여중이지 않은 유저").build());
    }
    private List<ChatMessage> getMyUnreadMessages(List<ChatMessage> messages, Long userId){
        return messages.stream()
                .filter(msg -> !msg.getUserId().equals(userId)) // 내가 보낸 메세지 제외
                .filter(msg -> msg.getUnreadCount() > 0 && !msg.getReadBy().contains(userId))
                .toList();
    }
    private String changeUnreadMessagesToRead(List<ChatMessage> unreadMessages, Long userId){
        if(!unreadMessages.isEmpty()){
            unreadMessages.forEach(msg -> msg.addReadBy(userId));
//            chatMessageRepository.saveAll(unreadMessages);
            return unreadMessages.get(0).getChatMessageId();
        }
        return null;
    }
}
