// const apiRequest = AuthFunc.apiRequest;
// const authHeader = AuthFunc.getAuthHeader;

let chatRoomId = null;
let loginUserId = null;
AuthFunc.primaryKey().then(pk => {
    loginUserId = pk;
    console.log(loginUserId + "님 환영합니다!");
    subscribeChatRoom();
    // subscribeChatMessage("68b295afd79c160bedea0603");
});


const socket = new SockJS("/ws-chat");
const stompClient = Stomp.over(socket);

//소켓 준비 대기
async function waitForSockJsAndStomp() {
    while (!(window.SockJS && window.Stomp)) {
        await new Promise(resolve => setTimeout(resolve, 50));
    }
}

function connectStomp() {
    return AuthFunc.apiRequest(() =>
        new Promise((resolve, reject) => {
            if (stompClient.connected) return resolve();
            stompClient.connect(AuthFunc.getAuthHeader(), resolve, reject);
        })
    );
}

function subscribeChatRoom() {
    connectStomp().then(() => {
        stompClient.subscribe("/rooms/" + loginUserId, (response) => {
            // console.log("채팅방 목록 업데이트 메시지 수신:", JSON.parse(response.body));
            const data = JSON.parse(response.body).success.responseData;

            renderChatRoom(data);
        });
    }).catch(error => {
        console.error(error)
    });
}

function subscribeChatMessage(roomId) {
    connectStomp().then(() => {
        stompClient.subscribe("/chat/" + roomId, (response) => {
            console.log(JSON.parse(response.body))
        });
    }).catch(error => {
        console.error(error)
    });
}


function createRoom() {
    const userId = document.getElementById("roomIdInput").value;
    if (!userId) return;
    AuthFunc.apiRequest(() =>
        axios.post("/api/chat/room",
            {},
            {
                headers: AuthFunc.getAuthHeader(),
                params: {targetUserId: userId}
            }
        )
    ).then(res => {
        chatRoomId = res.data.success.responseData.chatRoomId;

        alert("채팅방 생성됨! 방 ID: " + chatRoomId);
        connectRoom(chatRoomId);
    });

}

function connectRoom(roomId) {

    // jwt는 헤더에 Authorization: Bearer ... 로 붙음
    AuthFunc.apiRequest(() =>
        new Promise((resolve, reject) => {
            stompClient.connect(AuthFunc.getAuthHeader(), () => {
                stompClient.subscribe("/topic/" + roomId, (message) => {
                    console.log(message);
                    const msg = JSON.parse(message.body);
                    showMessage(msg);
                });
                alert("채팅방 " + roomId + " 연결됨!");
                resolve(); // 성공 시 resolve
            }, (error) => {
                reject(error); // 에러 발생 시 reject
            });
        })
    ).catch(error => {
        console.error("STOMP 연결 실패:", error);
    });

}

function send() {
    const content = document.getElementById("messageInput").value;
    if (!content) return;

    const requestBody = {
        chatRoomId: chatRoomId,
        content: content
    };

    AuthFunc.apiRequest(() =>
        axios.post("/api/chat/message",
            requestBody,
            {
                headers: AuthFunc.getAuthHeader()
            }
        )
    ).then(res => {
        console.log("보낸 메시지:", res);
    });

    document.getElementById("messageInput").value = "";
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

    data.forEach(room => {
        const li = createRoomLiTag(room);
        chatRoomList.appendChild(li);
    });
    showTotalUnreadBadge(data)
}

function updateTotalUnreadBadgeFromDOM() {
    // 모든 채팅방의 안읽은 뱃지(span.badge.bg-danger) 선택
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
        subscribeChatMessage(roomId);

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

        return `
            <div class="message-item ${messageClass} ${oldUnreadClass}" data-message-index="${index}">
                <div class="message-content">
                    ${!isMyMessage ? `<img src="${message.sender.profileImageUrl}" alt="${message.sender.nickname}" class="message-profile-img">` : ''}
                    <div class="message-text-area">
                        ${!isMyMessage ? `<div class="message-nickname">${message.sender.nickname}</div>` : ''}
                        <div class="message-bubble">${message.content}</div>
                        <div class="message-time">${messageTime}</div>
                    </div>
                    ${isMyMessage ? `<img src="${message.sender.profileImageUrl}" alt="${message.sender.nickname}" class="message-profile-img">` : ''}
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
    requestAnimationFrame(() => {
        adjustScrollPosition(messages);
    });
}

// 스크롤 위치 조정 함수
function adjustScrollPosition(messages) {
    const messagesContainer = document.querySelector('.chat-messages-container');

    // oldUnread가 true인 첫 번째 메시지 찾기
    const firstUnreadIndex = messages.findIndex(message => message.oldUnread === true);
    const container = document.getElementById("chatRoomMessages");
    container.scrollTop = container.scrollHeight;
    console.log("scrollTop:", container.scrollTop, "scrollHeight:", container.scrollHeight);


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


