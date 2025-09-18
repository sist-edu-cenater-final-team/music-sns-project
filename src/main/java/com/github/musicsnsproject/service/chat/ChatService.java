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
import org.springdoc.core.service.GenericResponseService;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class ChatService {
    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final MyUserRepository myUserRepository;
    private final MongoTemplate mongoTemplate;
    private final GenericResponseService responseBuilder;

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
     메시지 저장
     */
    public ChatMessageResponse saveMessage(ChatMessageRequest chatMessageRequest, Long senderId, Set<Long> activeReceiverIds) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatMessageRequest.getChatRoomId())
                .orElseThrow(() -> CustomNotFoundException.of().request(chatMessageRequest).customMessage("존재하지 않는 채팅방").build());

        Set<Long> otherActiveIds = listToFilterStream(id -> !id.equals(senderId), activeReceiverIds)
                .collect(Collectors.toUnmodifiableSet());
        ChatMessage message = ChatMessage.create(chatMessageRequest, senderId, otherActiveIds);
        ChatMessage saved = chatMessageRepository.save(message);
        ChatUserInfo sender = myUserRepository.findAllByIdForChatRoom(Set.of(senderId)).get(0);

        return ChatMessageResponse.of(saved, sender, chatRoom.getParticipants(), otherActiveIds);
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
                .flatMap(chatRoom ->
                        listToFilterStream(id -> !id.equals(userId), chatRoom.getParticipants()))
                .collect(Collectors.toUnmodifiableSet());
    }


    private <T, R> Map<T, R> listToKeyMap(Function<R, T> keyMapper, List<R> list) {
        return list.stream()
                .collect(Collectors.toMap(keyMapper, item -> item));
    }
    private <T> Stream<T> listToFilterStream(Predicate<T> predicate, Collection<T> list) {
        return list.stream()
                .filter(predicate);
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
        return listToFilterStream(id-> !id.equals(senderId), chatRoom.getParticipants())
                .toList();
    }
    /** 채팅방 메세지 전송 후 채팅방리스트에 반환용 쿼리콜 줄인 ver */
    public ChatRoomSendResponse getSendRoomMessage(ChatMessageResponse messageResponse){
        List<ChatUserInfo> allUserInfos = myUserRepository.findAllByIdForChatRoom(new HashSet<>(messageResponse.getParticipantIds()));
        List<ReceiversInfo> receivers = crateReceiversInfo(allUserInfos, messageResponse.getChatRoomId());
        return ChatRoomSendResponse.of(messageResponse.getChatRoomId(), messageResponse.getContent(), messageResponse.getSentAt(), receivers);
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

    /** 모든 유저들의 정보를 각각 수신자와 참여자로 한번씩 정리  */
    private List<ReceiversInfo> crateReceiversInfo(List<ChatUserInfo> allUserInfos, String chatRoomId) {
        List<ReceiversInfo> receivers = new ArrayList<>(allUserInfos.size());
        for (ChatUserInfo userInfo : allUserInfos) {
            List<ChatUserInfo> otherInfos =
                    listToFilterStream(info -> info.getUserId() != userInfo.getUserId(), allUserInfos)
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
        // 참여중인 유저 정보들
        List<ChatUserInfo> participants = myUserRepository.findAllByIdForChatRoom(new HashSet<>(chatRoom.getParticipants()));
        Map<Long, ChatUserInfo> userInfoMap = listToKeyMap(ChatUserInfo::getUserId, participants);
        // 메세지들
        List<ChatMessage> messages = chatMessageRepository.findByChatRoomIdOrderBySentAtAsc(roomId);

        if(messages.isEmpty())
            return createRoomResponse(userInfoMap, userId, roomId, List.of(), List.of());

        // 내가 안읽은 메세지들 내가 읽은걸로 변경 하고 가장 오래된 안읽은 메세지의 ID를 가져옴
        List<ChatMessage> unreadMessages = getMyUnreadMessages(messages, userId);
        String oldUnreadId = changeUnreadMessagesToRead(unreadMessages, userId);
        // 응답용 메세지 리스트
        List<ChatMessageResponse> chatMessageResponses = chatMessageListToResponseList(messages, userInfoMap, oldUnreadId);
        return createRoomResponse(userInfoMap, userId, roomId, chatMessageResponses, unreadMessages);
    }

    private List<ChatMessageResponse> chatMessageListToResponseList(List<ChatMessage> messages, Map<Long, ChatUserInfo> userInfoMap, String oldUnreadId) {
        List<ChatMessageResponse> responses = new ArrayList<>(messages.size());
        for (ChatMessage message : messages) {
            ChatUserInfo senderInfo = userInfoMap.get(message.getUserId());
            boolean oldUnread = message.getChatMessageId().equals(oldUnreadId);
            ChatMessageResponse response = ChatMessageResponse.of(
                    message,
                    senderInfo,
                    new ArrayList<>(userInfoMap.keySet()),
                    oldUnread
            );

            responses.add(response);
        }
        return responses;
    }

    private ChatRoomResponse createRoomResponse(Map<Long,ChatUserInfo> userInfoMap,
                                                long userId,
                                                String roomId,
                                                List<ChatMessageResponse> messageResponse,
                                                List<ChatMessage> unreadMessages) {
        List<ChatUserInfo> otherUsers = listToFilterStream(
                userinfo -> userinfo.getUserId() != userId,
                userInfoMap.values()
        ).toList();
        List<String> unreadMessageIds = unreadMessages.stream()
                .map(ChatMessage::getChatMessageId)
                .toList();
        return new ChatRoomResponse(roomId, userInfoMap.get(userId), otherUsers, messageResponse, unreadMessageIds);
    }


    private List<ChatUserInfo> getOtherUsersInfo(List<ChatUserInfo> participants, long userId) {
        return listToFilterStream(userInfo -> userInfo.getUserId() != userId, participants)
                .toList();
    }
    private ChatUserInfo getMeInfo(List<ChatUserInfo> participants, long userId) {
        return listToFilterStream(userInfo ->
                userInfo.getUserId() == userId, participants)
                .findFirst()
                .orElseThrow(() -> CustomNotFoundException.of().request(userId).customMessage("채팅방에 참여중이지 않은 유저").build());
    }
    private List<ChatMessage> getMyUnreadMessages(List<ChatMessage> messages, Long userId){
        return listToFilterStream(msg -> !msg.getUserId().equals(userId), messages) // 내가 보낸 메세지 제외
                .filter(msg -> !msg.getReadBy().contains(userId))
                .toList();
    }
    private String changeUnreadMessagesToRead(List<ChatMessage> unreadMessages, Long userId){
        if(!unreadMessages.isEmpty()){
            unreadMessages.forEach(msg -> msg.addReadBy(userId));
            chatMessageRepository.saveAll(unreadMessages);
            return unreadMessages.get(0).getChatMessageId();
        }
        return null;
    }
}
