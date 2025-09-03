// const apiRequest = AuthFunc.apiRequest;
// const authHeader = AuthFunc.getAuthHeader;

let loginUserId = null;
AuthFunc.primaryKey().then(async pk => {
    loginUserId = pk;
    console.log(pk + "님 환영합니다!");
    await connectStomp(pk)
    await subscribeChatRoom(pk);
    // subscribeChatMessage("68b295afd79c160bedea0603");
});


let stompClient = null;

//소켓 준비 대기
// async function waitForSockJsAndStomp() {
//     while (!(window.SockJS && window.Stomp)) {
//         await new Promise(resolve => setTimeout(resolve, 50));
//     }
// }
function connectStomp(pk) {
    return new Promise((resolve, reject) => {

        const socket = new SockJS("/ws-chat");
        stompClient = Stomp.over(socket);
        const principalHeader = {principal: pk};
        console.log("WebSocket 연결 시도...", principalHeader);

        stompClient.connect(
            principalHeader,
            () => {
                console.log("WebSocket 연결 성공");
                resolve(); // 연결 완료 시 Promise resolve
            },
            (error) => {
                console.error("WebSocket 연결 실패:", error);
                reject(error);
            }
        );
    });
}

function subscribeChatRoom(pk) {
    stompClient.subscribe("/rooms/" + pk, (response) => {
        // console.log("채팅방 목록 업데이트 메시지 수신:", JSON.parse(response.body));
        const data = JSON.parse(response.body).success.responseData;

        renderChatRoom(data);
    });
}

function chatRoomUnreadBadgeSetting(messageIds) {
    messageIds.forEach(messageId => {
        const badge = document.getElementById("unread:" + messageId);
        if (!badge) return;  // early return으로 가독성 향상

        const count = parseInt(badge.innerText, 10) || 0;  // NaN 방어
        const newCount = count - 1;

        if (newCount <= 0) {
            badge.remove();
        } else {
            badge.innerText = String(newCount);
        }
    });
}

let currentChatRoomSubscription = null; // 현재 채팅방 구독 객체 저장

function subscribeChatMessage(roomId) {
    // 기존 구독이 있으면 해제
    if (currentChatRoomSubscription) {
        currentChatRoomSubscription.unsubscribe();
    }
    // 새로운 구독 등록 및 저장
    currentChatRoomSubscription = stompClient.subscribe("/chat/" + roomId, (response) => {
        const data = JSON.parse(response.body).success;


        // showMessage(data);

        if (data.code === 200 && data.responseData) {
            const responseData = data.responseData;
            // console.log(responseData);
            chatRoomUnreadBadgeSetting(responseData);

            return;
        }


        if (data.code === 201 && data.responseData) {
            console.log("채팅 메시지 수신:", data);
            // 새로운 메시지 렌더링
            appendNewMessage(data.responseData);
        }
    });
}

function appendNewMessage(messageData) {
    const messagesContainer = document.getElementById('chatRoomMessages');
    if (!messagesContainer) return;

    const isMyMessage = messageData.sender.userId === loginUserId;
    const messageClass = isMyMessage ? 'my-message' : 'other-message';

    const messageDate = new Date(messageData.sentAt);
    const today = new Date();
    const yesterday = new Date(today);
    yesterday.setDate(today.getDate() - 1);

    const isToday = messageDate.toDateString() === today.toDateString();
    const isYesterday = messageDate.toDateString() === yesterday.toDateString();

    let messageTime;
    if (isToday) {
        messageTime = messageDate.toLocaleTimeString("ko-KR", {
            hour: "2-digit", minute: "2-digit"
        });
    } else if (isYesterday) {
        messageTime = "어제";
    } else {
        messageTime = `${messageDate.getFullYear()}년 ${String(messageDate.getMonth() + 1).padStart(2, "0")}월 ${String(messageDate.getDate()).padStart(2, "0")}일`;
    }

    // 날짜 구분선 체크 (마지막 메시지와 날짜가 다른지)
    const lastMessage = messagesContainer.lastElementChild;
    const dateSeparator = lastMessage && lastMessage.classList.contains('message-item')
        ? checkDateSeparator(messageDate, lastMessage)
        : '';

    // 안읽음 수 표시
    const unreadBadge = messageData.unreadCount > 0 ?
        `<span class="message-unread-badge" id="unread:${messageData.chatMessageId}">${messageData.unreadCount}</span>` : '';

    const messageHtml = `
        ${dateSeparator}
        <div class="message-item ${messageClass}" data-send-at="${messageData.sentAt}" data-message-index="new">
            <div class="message-content">
                ${!isMyMessage ? `<img src="${messageData.sender.profileImageUrl}" alt="${messageData.sender.nickname}" class="message-profile-img">` : ''}
                <div class="message-text-area">
                    ${!isMyMessage ? `<div class="message-nickname">${messageData.sender.nickname}</div>` : ''}
                    <div class="message-bubble-container">
                        <div class="message-bubble">${messageData.content}</div>
                        ${unreadBadge}
                    </div>
                    <div class="message-time">${messageTime}</div>
                </div>
            </div>
        </div>
    `;
    const isAtBottom = messagesContainer.scrollTop >= messagesContainer.scrollHeight - messagesContainer.clientHeight - 120;
    // 메시지 추가
    messagesContainer.insertAdjacentHTML('beforeend', messageHtml);

    // 내가 보낸 메시지면 스크롤을 맨 아래로
    if (isMyMessage || isAtBottom) {
        messagesContainer.scrollTop = messagesContainer.scrollHeight;
    } else {
        // 남이 보낸 메시지면 새 메시지 알림 표시
        showNewMessageNotification(messageData);
    }
}

// 새 메시지 알림 모달 표시
function showNewMessageNotification(messageData) {
    // 기존 알림 제거
    const chatRoomModal = document.getElementById('chatRoomModal');
    const chatMessagesContainer = document.getElementById('chatRoomMessages');
    const chatInputContainer = document.querySelector('.chat-input-container');
    if (!chatRoomModal || !chatMessagesContainer || !chatInputContainer) return;

    const existingNotification = chatRoomModal.querySelector('.new-message-notification');
    if (existingNotification) existingNotification.remove();

    // 알림 생성
    const notification = document.createElement('div');
    notification.className = 'new-message-notification';
    notification.innerHTML = `
        <div class="notification-sender">${messageData.sender.nickname}</div>
        <div class="notification-content">${messageData.content}</div>
    `;

    // chatRoomModal의 modal-body에 추가 (chat-input-container 바로 앞에)
    const modalBody = chatRoomModal.querySelector('.modal-body');
    modalBody.insertBefore(notification, chatInputContainer);

    // 애니메이션 효과
    setTimeout(() => {
        notification.classList.add('show');
    }, 100);

    // 클릭 시 스크롤 맨 아래 + 알림 제거
    notification.addEventListener('click', () => {
        chatMessagesContainer.scrollTop = chatMessagesContainer.scrollHeight;
        notification.remove();
    });

    // 4초 후 자동 제거
    setTimeout(() => {
        notification.classList.remove('show');
        setTimeout(() => {
            if (notification.parentNode) notification.remove();
        }, 300);
    }, 4000);
}



function checkDateSeparator(currentElementDate, lastMessageElement) {
    if (!lastMessageElement) return '';

    // data-send-at 속성에서 이전 메시지 날짜 가져오기
    const lastDateStr = lastMessageElement.dataset.sendAt;
    if (!lastDateStr) return '';

    const lastDate = new Date(lastDateStr);

    // 날짜가 다르면 구분선 생성
    if (currentElementDate.toDateString() !== lastDate.toDateString()) {
        const today = new Date();
        const yesterday = new Date(today);
        yesterday.setDate(today.getDate() - 1);

        const isToday = currentElementDate.toDateString() === today.toDateString();
        const isYesterday = currentElementDate.toDateString() === yesterday.toDateString();

        let dateText;
        if (isToday) {
            dateText = "오늘";
        } else if (isYesterday) {
            dateText = "어제";
        } else {
            dateText = `${currentElementDate.getFullYear()}년 ${String(currentElementDate.getMonth() + 1).padStart(2, "0")}월 ${String(currentElementDate.getDate()).padStart(2, "0")}일`;
        }

        return `
            <div class="date-separator">
                <span class="date-separator-text">${dateText}</span>
            </div>
        `;
    }

    return '';
}


function showMessage(msg) {
    const chatBox = document.getElementById("chatBox");
    const div = document.createElement("div");
    div.className = "msg " + (msg.senderId === userId ? "me" : "other");
    div.innerText = msg.userId + ": " + msg.content;
    chatBox.appendChild(div);
    chatBox.scrollTop = chatBox.scrollHeight;
}


document.addEventListener("DOMContentLoaded", function () {
    const chatRoomModal = document.getElementById('chatRoomModal');
    // 모달이 완전히 닫혔을 때 구독 해제
    chatRoomModal.addEventListener('hidden.bs.modal', function () {
        if (currentChatRoomSubscription) {
            console.log('채팅방 구독 해제');
            currentChatRoomSubscription.unsubscribe();
            currentChatRoomSubscription = null;
        }
    });


    // 서버에서 채팅방 리스트 가져오기
    AuthFunc.apiRequest(() =>
        axios.get(`${ctxPath}/api/chat/my-rooms`, {
            headers: AuthFunc.getAuthHeader()
        })
    ).then(response => {
        console.log(response);
        const data = response.data.success.responseData;
        renderChatRooms(data);
    }).catch(error => {
        console.error("채팅방 목록 불러오기 실패", error);
    });

    const btnTalk = document.getElementById("btnTalk");
    const chatModal = new bootstrap.Modal(document.getElementById("chatModal"));

    btnTalk.addEventListener("click", function () {
        // 모달 열기
        chatModal.show();
    });
});

function renderChatRoom(room) {
    const chatRoomList = document.getElementById("chatRoomList");
    const existingRoom = document.getElementById(room.chatRoomId);
    const emptyMsg = document.getElementById("emptyRoomMsg");
    if (emptyMsg)
        emptyMsg.remove();
    if (existingRoom)
        chatRoomList.removeChild(existingRoom);
    const li = createRoomLiTag(room);

    chatRoomList.insertBefore(li, chatRoomList.firstChild);
    updateTotalUnreadBadgeFromDOM()
}

// 채팅방 목록 렌더링


function renderChatRooms(data) {
    const chatRoomList = document.getElementById("chatRoomList");
    chatRoomList.innerHTML = ""; // 초기화
    if(!data || data.length === 0){
        chatRoomList.innerHTML = `
        <li class="list-group-item text-center" id="emptyRoomMsg">
            <i class="bi bi-chat-dots empty-icon"></i>
            <div class="empty-title">참여중인 대화가 없습니다</div>
            <div class="empty-subtitle">새로운 대화를 시작해보세요</div>
        </li>
    `;
        return;
    }

    data.forEach(room => {
        const li = createRoomLiTag(room);
        chatRoomList.appendChild(li);
    });
    showTotalUnreadBadge(data)
}

function updateTotalUnreadBadgeFromDOM() {
    // 모든 채팅방의 안읽은 뱃지(#chatRoomList .unread-count-badge) 선택
    const badges = document.querySelectorAll("#chatRoomList .unread-count-badge");
    let totalUnread = 0;
    badges.forEach(badge => {
        const count = parseInt(badge.innerText, 10);
        if (!isNaN(count)) totalUnread += count;
    });

    let totalBadge = document.getElementById("totalUnreadBadge");
    if (!totalBadge) {
        totalBadge = document.createElement("span");
        totalBadge.id = "totalUnreadBadge";
        totalBadge.className = "unread-badge";
        document.getElementById("btnTalk").appendChild(totalBadge);
    }
    totalBadge.style.display = totalUnread > 0 ? "inline-block" : "none";
    totalBadge.innerText = totalUnread;
}


function showTotalUnreadBadge(data) {
    const totalUnread = data.reduce((sum, room) => sum + (room.unreadCount || 0), 0);
    let badge = document.getElementById("totalUnreadBadge");
    if (!badge) {
        badge = document.createElement("span");
        badge.id = "totalUnreadBadge";
        badge.className = "unread-badge";
        document.getElementById("btnTalk").appendChild(badge);
    }
    badge.style.display = totalUnread > 0 ? "inline-block" : "none";
    badge.innerText = totalUnread;
}

// 프로필 이미지를 클릭했을 때 원본 모달 표시
function openProfileImageModal(imgUrl) {
    const modalImg = document.getElementById("profileImageModalImg");
    modalImg.src = imgUrl;

    const profileModal = new bootstrap.Modal(document.getElementById("profileImageModal"));
    profileModal.show();
}


//채팅방 로직

// 채팅방 모달 열기
function openChatRoom(roomId) {
    AuthFunc.apiRequest(() =>
        axios.get(`${ctxPath}/api/chat/${roomId}`, {
            headers: AuthFunc.getAuthHeader()
        })
    ).then(response => {
        const data = response.data.success.responseData;
        renderChatRoomModal(data);

        const chatRoomModal = new bootstrap.Modal(document.getElementById("chatRoomModal"));
        chatRoomModal.show();

        // 채팅방 구독
        console.log(roomId + ' 구독 시작');
        subscribeChatMessage(roomId);
        //룸 뱃지 없애기
        const roomLi = document.getElementById(roomId);
        if (roomLi) {
            const badge = roomLi.querySelector('.unread-count-badge');
            if (badge) badge.remove();
        }
        updateTotalUnreadBadgeFromDOM();
    }).catch(error => {
        console.error("채팅방 불러오기 실패", error);
    });
}





// 채팅방 모달 렌더링
// 채팅방 모달 렌더링 함수 수정
function renderChatRoomModal(roomData) {
    console.log(roomData);
    const otherUsers = roomData.otherUsers;
    const messages = roomData.messages;

    // 참여자 프로필 이미지들
    const participantImagesHtml = otherUsers.map(user =>
        `<img src="${user.profileImageUrl}" alt="${user.nickname}" class="participant-img">`
    ).join('');

    // 참여자 닉네임들
    const participantNames = otherUsers.map(user => user.nickname).join(', ');

    // 메시지들 렌더링
    const messagesHtml = messages.map((message, index) => {
        const isMyMessage = message.sender.userId === loginUserId;
        const messageClass = isMyMessage ? 'my-message' : 'other-message';
        const oldUnreadClass = message.oldUnread ? 'old-unread-message' : '';

        const today = new Date();
        const yesterday = new Date(today);
        yesterday.setDate(today.getDate() - 1);
        const messageDate = new Date(message.sentAt);
        const isToday = messageDate.toDateString() === today.toDateString();
        const isYesterday = messageDate.toDateString() === yesterday.toDateString();

        let messageTime;
        if (isToday) {
            messageTime = messageDate.toLocaleTimeString("ko-KR", {
                hour: "2-digit", minute: "2-digit"
            });
        } else if (isYesterday) {
            messageTime = "어제";
        } else {
            messageTime = `${messageDate.getFullYear()}년 ${String(messageDate.getMonth() + 1).padStart(2, "0")}월 ${String(messageDate.getDate()).padStart(2, "0")}일`;
        }
        // 날짜 구분선 로직
        let dateSeparator = '';
        const prevMessage = index > 0 ? messages[index - 1] : null;

        if (!prevMessage || new Date(prevMessage.sentAt).toDateString() !== messageDate.toDateString()) {
            let dateText;
            if (isToday) {
                dateText = "오늘";
            } else if (isYesterday) {
                dateText = "어제";
            } else {
                dateText = `${messageDate.getFullYear()}년 ${String(messageDate.getMonth() + 1).padStart(2, "0")}월 ${String(messageDate.getDate()).padStart(2, "0")}일`;
            }

            dateSeparator = `
            <div class="date-separator">
                <span class="date-separator-text">${dateText}</span>
            </div>
        `;
        }

        // 안읽음 수 표시
        const unreadBadge = message.unreadCount > 0 ?
            `<span class="message-unread-badge" id="unread:${message.chatMessageId}">${message.unreadCount}</span>` : '';

        return `
        ${dateSeparator}
        <div class="message-item ${messageClass} ${oldUnreadClass}" data-send-at="${message.sentAt}" data-message-index="${index}">
            <div class="message-content">
                ${!isMyMessage ? `<img src="${message.sender.profileImageUrl}" alt="${message.sender.nickname}" class="message-profile-img">` : ''}
                <div class="message-text-area">
                    ${!isMyMessage ? `<div class="message-nickname">${message.sender.nickname}</div>` : ''}
                    <div class="message-bubble-container">
                        <div class="message-bubble">${message.content}</div>
                        ${unreadBadge}
                    </div>
                    <div class="message-time">${messageTime}</div>
                </div>
            </div>
        </div>
    `;
    }).join('');

    // 모달 내용 업데이트
    document.getElementById('chatRoomParticipantImages').innerHTML = participantImagesHtml;
    document.getElementById('chatRoomParticipantNames').textContent = participantNames;
    document.getElementById('chatRoomMessages').innerHTML = messagesHtml;
    document.getElementById('chatRoomModal').dataset.roomId = roomData.chatRoomId;

    // 스크롤 위치 조정
    const modalEl = document.getElementById('chatRoomModal');
    // 이미 떠있는 상태면 바로, 아니면 shown 시점에 실행
    const run = () => {
        // 한 프레임 뒤 + 이미지 로드 후에 스크롤 조정
        requestAnimationFrame(() => adjustScrollPositionAfterPaint(roomData.messages));
    };

    if (modalEl.classList.contains('show')) {
        run();
    } else {
        modalEl.addEventListener('shown.bs.modal', () => {
            run();
        }, {once: true});
    }
}

function waitImages(container) {
    const imgs = Array.from(container.querySelectorAll('img'));
    const pendings = imgs.filter(img => !img.complete);

    if (pendings.length === 0) return Promise.resolve();

    return new Promise(resolve => {
        let left = pendings.length;
        const done = () => {
            if (--left === 0) resolve();
        };
        pendings.forEach(img => {
            img.addEventListener('load', done, {once: true});
            img.addEventListener('error', done, {once: true});
        });
    });
}


async function adjustScrollPositionAfterPaint(messages) {
    const container = document.getElementById('chatRoomMessages');
    if (!container) return;

    // 레이아웃 확정까지 2프레임 정도 기다리면 안전
    await new Promise(r => requestAnimationFrame(() => requestAnimationFrame(r)));

    // 이미지 로드 대기
    await waitImages(container);

    // 1) oldUnread가 true인 첫 메시지로 맞추기
    const firstUnreadIndex = messages.findIndex(m => m.oldUnread === true);
    if (firstUnreadIndex >= 0) {
        const target = container.querySelector(`[data-message-index="${firstUnreadIndex}"]`);
        if (target) {
            // 읽음 표시 구분선 추가
            const readIndicator = document.createElement('div');
            readIndicator.className = 'read-indicator';
            readIndicator.innerHTML = `
            <div class="read-indicator-line"></div>
            <span class="read-indicator-text">여기까지 읽으셨습니다</span>
        `;
            // 타겟 메시지 바로 위에 삽입
            target.parentNode.insertBefore(readIndicator, target);
            // 컨테이너 최상단 정렬
            container.scrollTop = target.offsetTop - container.offsetTop - 30;
            // 또는 필요 시:
            // target.scrollIntoView({ block: 'start' });
        }
    } else {
        // 2) 없으면 맨 아래
        container.scrollTop = container.scrollHeight;
    }
}


// 메시지 전송
function sendChatMessage() {
    const input = document.getElementById('chatMessageInput');
    const content = input.value.trim();
    const roomId = document.getElementById('chatRoomModal').dataset.roomId;

    if (!content || !roomId) return;

    const requestBody = {
        chatRoomId: roomId,
        content: content
    };

    AuthFunc.apiRequest(() =>
        axios.post("/api/chat/message", requestBody, {
            headers: AuthFunc.getAuthHeader()
        })
    ).then(res => {
        input.value = "";
        console.log("메시지 전송 성공:", res);
    }).catch(error => {
        console.error("메시지 전송 실패:", error);
    });
}

// Enter 키로 메시지 전송
function handleChatInputKeydown(event) {
    if (event.key === 'Enter' && !event.shiftKey) {
        event.preventDefault();
        sendChatMessage();
    }
}

// 기존 createRoomLiTag 함수 수정 - 클릭 이벤트 변경
function createRoomLiTag(room) {
    const lastMsgDate = new Date(room.lastMessageTime);
    const today = new Date();
    const yesterday = new Date(today);
    yesterday.setDate(today.getDate() - 1);

    const isToday = lastMsgDate.toDateString() === today.toDateString();
    const isYesterday = lastMsgDate.toDateString() === yesterday.toDateString();

    let lastTime;
    if (isToday) {
        lastTime = lastMsgDate.toLocaleTimeString("ko-KR", {
            hour: "2-digit", minute: "2-digit"
        });
    } else if (isYesterday) {
        lastTime = "어제";
    } else {
        lastTime = `${lastMsgDate.getFullYear()}년 ${String(lastMsgDate.getMonth() + 1).padStart(2, "0")}월 ${String(lastMsgDate.getDate()).padStart(2, "0")}일`;
    }

    const li = document.createElement("li");
    li.className = "list-group-item d-flex align-items-center gap-2";
    li.id = room.chatRoomId;
    li.innerHTML = `
    <img src="${room.otherUsers[0].profileImageUrl}" 
         alt="프로필" 
         class="rounded-circle"
         onclick="openProfileImageModal('${room.otherUsers[0].profileImageUrl}')">
    <div class="room-list-right">
        <div class="flex-grow-1">
            <div class="fw-bold">${room.otherUsers[0].nickname}</div>
            <div class="text-muted small">${room.lastMessage || ''}</div>
        </div>
        <div class="text-end">
            <div class="small text-muted last-message-time">${lastTime}</div>
            ${room.unreadCount > 0 ?
        `<span class="badge bg-danger unread-count-badge">${room.unreadCount}</span>` :
        ``}
        </div>
    </div>
`;

    // 채팅방 클릭 이벤트 수정
    li.querySelector(".room-list-right").addEventListener("click", () => {
        openChatRoom(room.chatRoomId);
    });

    return li;
}

function createOrGetRoomId(userId) {
    return AuthFunc.apiRequest(()=>axios.post(`${ctxPath}/api/chat/room`, {},{
        headers: AuthFunc.getAuthHeader(),
        params: { targetUserId: userId }
    })).then(response => {
        return response.data.success.responseData.chatRoomId;
    }).catch(error => {
        console.error(error);
        alert("채팅방 생성에 실패했습니다.");
    })

}

async function goToMessage(userId) {
    alert(userId);
    const chatRoomId = await createOrGetRoomId(userId);
    if(chatRoomId){
        const btnTalk = document.getElementById("btnTalk");
        await openChatRoom(chatRoomId);
        btnTalk.click();
    }


}