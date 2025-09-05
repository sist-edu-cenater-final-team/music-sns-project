//부트스트랩 버전 감지 및 통합 처리
function getBootstrapVersion() {
    if (typeof bootstrap !== 'undefined' && bootstrap.Modal && bootstrap.Modal.getInstance) {
        return 5;
    } else if (typeof $ !== 'undefined' && $.fn.modal) {
        return 4; // Bootstrap 4
    }
    return null;
}

// 모달 인스턴스 생성 함수
function createModal(elementId) {
    const bootstrapVersion = getBootstrapVersion();
    const element = document.getElementById(elementId);

    if (bootstrapVersion === 5) {
        return new bootstrap.Modal(element);
    } else if (bootstrapVersion === 4) {
        return $(element); // jQuery 객체 반환
    }
    return null;
}

// 모달 표시 함수
function showModal(modal) {
    const bootstrapVersion = getBootstrapVersion();

    if (bootstrapVersion === 5) {
        modal.show();
    } else if (bootstrapVersion === 4) {
        modal.modal('show');
    }
}

// 모달 숨김 함수
function hideModal(modal) {
    const bootstrapVersion = getBootstrapVersion();

    if (bootstrapVersion === 5) {
        modal.hide();
    } else if (bootstrapVersion === 4) {
        modal.modal('hide');
    }
}


let loginUserId = null;
AuthFunc.primaryKey().then(async pk => {
    loginUserId = pk;
    console.log(pk + "님 환영합니다!");
    await connectStomp(pk)
    await subscribeChatRoom(pk);
    // subscribeChatMessage("68b295afd79c160bedea0603");
});


let stompClient = null;
let reconnectAttempts = 0;
const maxReconnectAttempts = 5;
let reconnectInterval = 1000; // 1초부터 시작

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
                reconnectAttempts = 0; // 성공 시 재연결 시도 횟수 초기화
                reconnectInterval = 1000; // 재연결 간격 초기화
                resolve(); // 연결 완료 시 Promise resolve
            },
            (error) => {
                console.error("WebSocket 연결 실패:", error);
                handleReconnect(pk, resolve, reject);
            }
        );
        // 연결 해제 시 재연결 시도
        socket.onclose = function () {
            console.log("WebSocket 연결이 종료되었습니다.");
            handleReconnect(pk);
        };
    });
}

function handleReconnect(pk, resolve = null, reject = null) {
    if (reconnectAttempts < maxReconnectAttempts) {
        reconnectAttempts++;
        console.log(`재연결 시도 ${reconnectAttempts}/${maxReconnectAttempts} (${reconnectInterval}ms 후)`);

        setTimeout(() => {
            connectStomp(pk).then(() => {
                if (resolve) resolve();
                // 재연결 성공 시 기존 구독들 복원
                restoreSubscriptions();
            }).catch((error) => {
                if (reject) reject(error);
            });
        }, reconnectInterval);

        // 재연결 간격을 점진적으로 증가 (최대 30초)
        reconnectInterval = Math.min(reconnectInterval * 2, 30000);
    } else {
        console.error("최대 재연결 시도 횟수를 초과했습니다.");
        if (reject) reject(new Error("연결 실패"));
    }
}

// 재연결 시 기존 구독들을 복원하는 함수
function restoreSubscriptions() {
    if (loginUserId) {
        subscribeChatRoom(loginUserId);

        // 현재 열린 채팅방이 있다면 다시 구독
        const currentRoomId = document.getElementById('chatRoomModal')?.dataset?.roomId;
        if (currentRoomId) {
            subscribeChatMessage(currentRoomId);
        }
    }
}

function subscribeChatRoom(pk) {
    stompClient.subscribe("/rooms/" + pk, (response) => {
        // console.log("채팅방 목록 업데이트 메시지 수신:", JSON.parse(response.body));
        const data = JSON.parse(response.body).success.responseData;
        console.log(data);
        renderChatRoom(data);
        const chatToast = data.chatToast;
        const senderId = chatToast.sender.userId;
        const otherActiveUserIds = chatToast.otherActiveUserIds;
        if(senderId === loginUserId || (Array.isArray(otherActiveUserIds) && otherActiveUserIds.includes(loginUserId)))
            return;
        // 알림 표시
        showNewMessageToast(chatToast);
    });
}

function chatRoomUnreadBadgeSetting(messageIds) {
    messageIds.forEach(messageId => {
        const badge = document.getElementById("unread:" + messageId);
        if (!badge) return;

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
    const dateSeparator = checkDateSeparator(messageDate, lastMessage);


    // 안읽음 수 표시
    const unreadBadge = messageData.unreadCount > 0 ?
        `<span class="message-unread-badge" id="unread:${messageData.chatMessageId}">${messageData.unreadCount}</span>` : '';

    const nickname = messageData.sender.nickname || '알 수 없는 사용자';
    const profileMessage = messageData.sender.profileMessage || '';
    const profileImageUrl = messageData.sender.profileImageUrl || ctxPath + '/images/default-profile.png';

    const messageHtml = `
        ${dateSeparator}
        <div class="message-item ${messageClass}" data-send-at="${messageData.sentAt}" data-message-index="new">
            <div class="message-content">
                ${!isMyMessage ? `<img src="${messageData.sender.profileImageUrl}" alt="${messageData.sender.nickname}" class="message-profile-img" onclick="openProfileImageModal('${nickname}', '${profileMessage}', '${profileImageUrl}')">` : ''}
                <div class="message-text-area">
                    ${!isMyMessage ? `<div class="message-nickname">${messageData.sender.nickname}</div>` : ''}
                    <div class="message-bubble-container">
                        <div class="message-bubble" id="${messageData.chatMessageId}">${messageData.content}</div>
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

    const profileImageUrl = messageData.sender.profileImageUrl || ctxPath + '/images/default-profile.png';
    const nickname = messageData.sender.nickname || '알 수 없는 사용자';

    notification.innerHTML = `
        <div class="notification-content">
            <div class="notification-left">
                <img src="${profileImageUrl}" alt="${nickname}" class="notification-profile-img">
                <div class="notification-text">
                    <div class="notification-sender">${nickname}</div>
                    <div class="notification-message">${messageData.content}</div>
                </div>
            </div>
            <div class="notification-right">
                <i class="bi bi-chevron-up notification-icon"></i>
                <div class="notification-hint">클릭하여 확인</div>
            </div>
        </div>
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

    // 5초 후 자동 제거
    setTimeout(() => {
        notification.classList.remove('show');
        setTimeout(() => {
            if (notification.parentNode) notification.remove();
        }, 300);
    }, 5000);
}


function checkDateSeparator(currentElementDate, lastMessageElement) {
    // 이전 메시지가 없으면 (첫 번째 메시지) 현재 날짜 구분선 생성
    // console.log(lastMessageElement);
    if (!lastMessageElement.dataset.sendAt) {
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
    const chatModal = document.getElementById('chatModal');
    const bootstrapVersion = getBootstrapVersion();
    // 모달이 완전히 닫혔을 때 구독 해제
    // 부트스트랩 버전에 따른 이벤트 처리
    if (bootstrapVersion === 5) {
        // 채팅방 모달 ESC 키 이벤트 차단
        chatRoomModal.addEventListener('keydown', function (e) {
            if (e.key === 'Escape') {
                e.stopPropagation(); // 이벤트 전파 차단
                const modal = bootstrap.Modal.getInstance(chatRoomModal);
                if (modal) {
                    modal.hide();
                }
            }
        });
        chatRoomModal.addEventListener('hidden.bs.modal', function () {
            if (currentChatRoomSubscription) {
                console.log('채팅방 구독 해제');
                currentChatRoomSubscription.unsubscribe();
                currentChatRoomSubscription = null;
            }

            // 채팅방 목록 모달에 포커스 설정
            const chatModalElement = document.getElementById('chatModal');
            if (chatModalElement && chatModalElement.classList.contains('show')) {
                chatModalElement.focus();
                // 또는 모달 내부의 특정 요소에 포커스
                const firstFocusableElement = chatModalElement.querySelector('button, [href], input, select, textarea, [tabindex]:not([tabindex="-1"])');
                if (firstFocusableElement) {
                    firstFocusableElement.focus();
                }
            }
        });
        // 채팅 목록 모달도 ESC 이벤트 관리
        chatModal.addEventListener('keydown', function (e) {
            if (e.key === 'Escape') {
                // 채팅방 모달이 열려있으면 ESC 차단
                if (chatRoomModal.classList.contains('show')) {
                    e.preventDefault();
                    e.stopPropagation();
                    return false;
                }
            }
        });
    } else if (bootstrapVersion === 4) {
        $(chatRoomModal).on('hidden.bs.modal', function () {
            if (currentChatRoomSubscription) {
                console.log('채팅방 구독 해제');
                currentChatRoomSubscription.unsubscribe();
                currentChatRoomSubscription = null;
            }
            // 채팅방 목록 모달에 포커스
            if ($('#chatModal').hasClass('show')) {
                $('#chatModal').focus();
            }
        });
    }


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
    // const chatModal = new bootstrap.Modal(document.getElementById("chatModal"));


    btnTalk.addEventListener("click", function () {
        if (bootstrapVersion === 5) {
            const chatModal = new bootstrap.Modal(document.getElementById("chatModal"));
            chatModal.show();
        } else if (bootstrapVersion === 4) {
            $('#chatModal').modal('show');
        }
    });
    // 채팅 목록 모달 닫기 버튼
    const chatModalCloseBtn = document.querySelector('#chatModal .modal-close-btn');
    if (chatModalCloseBtn) {
        chatModalCloseBtn.addEventListener('click', function () {
            // const bootstrapVersion = getBootstrapVersion();
            if (bootstrapVersion === 5) {
                const modal = bootstrap.Modal.getInstance(document.getElementById('chatModal'));
                if (modal) modal.hide();
            } else if (bootstrapVersion === 4) {
                $('#chatModal').modal('hide');
            }
        });
    }

    // 채팅방 모달 닫기 버튼
    const chatRoomModalCloseBtn = document.querySelector('#chatRoomModal .modal-close-btn');
    if (chatRoomModalCloseBtn) {
        chatRoomModalCloseBtn.addEventListener('click', function () {
            // const bootstrapVersion = getBootstrapVersion();
            if (bootstrapVersion === 5) {
                const modal = bootstrap.Modal.getInstance(document.getElementById('chatRoomModal'));
                if (modal) modal.hide();
            } else if (bootstrapVersion === 4) {
                $('#chatRoomModal').modal('hide');
            }
        });
    }
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
    if (!data || data.length === 0) {
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
function openProfileImageModal(nickname, profileMessage, imageUrl) {
    // 이미지 설정
    const modalImg = document.getElementById("profileImageModalImg");
    modalImg.src = imageUrl;
    // modalImg.style.backgroundImage =  "url("+imageUrl+")";

    // 닉네임 설정
    const modalNickname = document.getElementById("profileModalNickname");
    modalNickname.textContent = nickname || "알 수 없는 사용자";

    // 상태메시지 설정
    const modalMessage = document.getElementById("profileModalMessage");
    modalMessage.textContent = profileMessage || "";

    // 모달 배경 클릭 시 닫기 이벤트 추가
    const modalBody = document.querySelector('.simple-profile-body');
    modalBody.onclick = function (e) {
        if (e.target === modalBody || e.target.closest('.profile-image-overlay')) {
            const bootstrapVersion = getBootstrapVersion();
            if (bootstrapVersion === 5) {
                const modal = bootstrap.Modal.getInstance(document.getElementById("profileImageModal"));
                if (modal) modal.hide();
            } else if (bootstrapVersion === 4) {
                $('#profileImageModal').modal('hide');
            }
        }
    };

    // 부트스트랩 버전에 따른 모달 표시
    const bootstrapVersion = getBootstrapVersion();
    if (bootstrapVersion === 5) {
        const profileModal = new bootstrap.Modal(document.getElementById("profileImageModal"));
        profileModal.show();
    } else if (bootstrapVersion === 4) {
        $('#profileImageModal').modal('show');
    }
}


//채팅방 로직

// 채팅방 채팅내역 모달 열기
function openChatRoom(roomId) {
    AuthFunc.apiRequest(() =>
        axios.get(`${ctxPath}/api/chat/${roomId}`, {
            headers: AuthFunc.getAuthHeader()
        })
    ).then(response => {
        const data = response.data.success.responseData;

        renderChatRoomModal(data);


        // 부트스트랩 버전에 따른 모달 표시
        const bootstrapVersion = getBootstrapVersion();
        if (bootstrapVersion === 5) {
            const chatRoomModal = new bootstrap.Modal(document.getElementById("chatRoomModal"), {
                keyboard: true // ESC 키 허용하되 이벤트 처리는 위에서 관리
            });
            chatRoomModal.show();
            // 모달이 완전히 표시된 후 포커스 설정
            document.getElementById("chatRoomModal").addEventListener('shown.bs.modal', function () {
                const messageInput = document.getElementById('chatMessageInput');
                if (messageInput) {
                    messageInput.focus();
                }
            }, {once: true});
        } else if (bootstrapVersion === 4) {
            $('#chatRoomModal').modal('show');
            $('#chatRoomModal').on('shown.bs.modal', function () {
                $('#chatMessageInput').focus();
            });
        }

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


// 채팅방 내부 모달 렌더링 함수 수정
function renderChatRoomModal(roomData) {
    const otherUsers = roomData.otherUsers;
    const messages = roomData.messages;
    console.log(otherUsers);


    // 참여자 프로필 이미지들
    const participantImagesHtml = otherUsers.map(user => {
            const profileImageUrl = user.profileImageUrl || ctxPath + '/images/default-profile.png';
            const nickname = user.nickname || '알 수 없는 사용자';
            const profileMessage = user.profileMessage || '';
            return `<img src="${profileImageUrl}" alt="${nickname}" class="participant-img" onclick="openProfileImageModal('${nickname}', '${profileMessage}', '${profileImageUrl}')">`
        }
    ).join('');

    // 참여자 닉네임들
    const participantNames = otherUsers.map(user => user.nickname).join(', ');


    // 모달 내용 업데이트
    document.getElementById('chatRoomParticipantImages').innerHTML = participantImagesHtml;
    document.getElementById('chatRoomParticipantNames').textContent = participantNames;
    document.getElementById('chatRoomModal').dataset.roomId = roomData.chatRoomId;

    if (!messages || messages.length === 0) {
        document.getElementById('chatRoomMessages').innerHTML = `
        <li class="list-group-item text-center" id="emptyRoomMsg">
            <i class="bi bi-chat-dots empty-icon"></i>
            <div class="empty-title">서로 나눈 대화 내역이 없습니다.</div>
            <div class="empty-subtitle">새로운 대화를 시작해보세요</div>
        </li>
    `;
        return;
    }


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

        const profileImageUrl = message.sender.profileImageUrl || ctxPath + '/images/default-profile.png';
        const nickname = message.sender.nickname || '알 수 없는 사용자';
        const profileMessage = message.sender.profileMessage || '';

        return `
        ${dateSeparator}
        <div class="message-item ${messageClass} ${oldUnreadClass}" data-send-at="${message.sentAt}" data-message-index="${index}">
            <div class="message-content">
                ${!isMyMessage ? `<img src="${message.sender.profileImageUrl}" alt="${message.sender.nickname}" class="message-profile-img" onclick="openProfileImageModal('${nickname}', '${profileMessage}', '${profileImageUrl}')">` : ''}
                <div class="message-text-area">
                    ${!isMyMessage ? `<div class="message-nickname">${message.sender.nickname}</div>` : ''}
                    <div class="message-bubble-container">
                        <div class="message-bubble" id="${message.chatMessageId}">${message.content}</div>
                        ${unreadBadge}
                    </div>
                    <div class="message-time">${messageTime}</div>
                </div>
            </div>
        </div>
    `;
    }).join('');


    document.getElementById('chatRoomMessages').innerHTML = messagesHtml;

    // 스크롤 위치 조정 (안읽은 메세지 기준)
    const modalEl = document.getElementById('chatRoomModal');
    // 이미 떠있는 상태면 바로, 아니면 shown 시점에 실행
    const run = () => {
        // 한 프레임 뒤 + 이미지 로드 후에 스크롤 조정
        requestAnimationFrame(() => adjustScrollPositionAfterPaint(roomData.messages));
    };
    const bootstrapVersion = getBootstrapVersion();
    if (modalEl.classList.contains('show')) {
        run();
    } else {
        // 부트스트랩 버전에 따른 이벤트 처리 분리
        if (bootstrapVersion === 5) {
            modalEl.addEventListener('shown.bs.modal', () => {
                run();
            }, {once: true});
        } else if (bootstrapVersion === 4) {
            $('#chatRoomModal').one('shown.bs.modal', () => {
                run();
            });
        }
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
    // alert('adjustScrollPositionAfterPaint 실행')
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

    const imageUrl = room.otherUsers[0]?.profileImageUrl;
    const nickname = room.otherUsers[0]?.nickname || "알 수 없음";
    const profileMessage = room.otherUsers[0]?.profileMessage || '';
    const li = document.createElement("li");
    li.className = "list-group-item d-flex align-items-center gap-2";
    li.id = room.chatRoomId;
    li.innerHTML = `
    <img src="${imageUrl}" 
         alt="프로필" 
         class="rounded-circle"
         onclick="openProfileImageModal('${nickname}', '${profileMessage}', '${imageUrl}')">
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
    return AuthFunc.apiRequest(() => axios.post(`${ctxPath}/api/chat/room`, {}, {
        headers: AuthFunc.getAuthHeader(),
        params: {targetUserId: userId}
    })).then(response => {
        return response.data.success.responseData.chatRoomId;
    }).catch(error => {
        console.error(error);
        alert("채팅방 생성에 실패했습니다.");
    })

}

async function goToMessage(userId) {
    // alert(userId);
    const chatRoomId = await createOrGetRoomId(userId);
    if (chatRoomId) {
        const btnTalk = document.getElementById("btnTalk");
        btnTalk.click();

        setTimeout(() => {
            openChatRoom(chatRoomId);
        }, 500);
    }
}

const audio = new Audio(ctxPath + '/sounds/chat-notification.mp3');
// 알림음 재생 함수
function playNotificationSound() {
    try {

        // 볼륨 설정 (0.0 ~ 1.0)
        audio.volume = 1.0;

        // 재생
        audio.play().catch(error => {
            console.log('알림음 재생 실패:', error);
            // 브라우저 자동재생 정책으로 실패할 수 있음
        });
    } catch (error) {
        console.log('알림음 생성 실패:', error);
    }
}


// 새 메시지 알림 표시 함수
function showNewMessageToast(messageData) {
    // alert('showNewMessageToast 실행');
    playNotificationSound()
    const { chatRoomId, chatMessageId, sender, content } = messageData;

    // 기존 토스트가 있다면 제거
    const existingToast = document.querySelector('.new-message-toast');
    if (existingToast) {
        existingToast.remove();
    }

    // 토스트 엘리먼트 생성
    const toast = document.createElement('div');
    toast.className = 'new-message-toast';
    toast.innerHTML = `
        <div class="toast-content">
            <img src="${sender.profileImageUrl || ctxPath + '/images/default-profile.png'}" 
                 alt="${sender.nickname}" class="toast-profile-img">
            <div class="toast-message-info">
                <div class="toast-nickname">${sender.nickname}</div>
                <div class="toast-message">${content}</div>
            </div>
            <i class="bi bi-chat-dots toast-icon"></i>
        </div>
    `;

    // 클릭 이벤트 추가
    toast.addEventListener('click', () => {
        hideNewMessageToast(toast);
        openChatRoomAndHighlightMessage(chatRoomId, chatMessageId);
    });

    // 문서에 추가
    document.body.appendChild(toast);

    // 애니메이션 시작
    setTimeout(() => {
        toast.classList.add('show');
    }, 100);

    // 3초 후 자동 숨김
    setTimeout(() => {
        hideNewMessageToast(toast);
    }, 3000);
}

// 토스트 숨김 함수
function hideNewMessageToast(toast) {
    if (!toast || !toast.parentNode) return;

    toast.classList.remove('show');
    setTimeout(() => {
        if (toast.parentNode) {
            toast.parentNode.removeChild(toast);
        }
    }, 400);
}

// 채팅방 열기 및 메시지 하이라이트 함수
function openChatRoomAndHighlightMessage(chatRoomId, chatMessageId) {
    // 채팅 목록 모달이 열려있지 않다면 열기
    const chatModal = document.getElementById('chatModal');
    if (!chatModal.classList.contains('show')) {
        const btnTalk = document.getElementById('btnTalk');
        btnTalk.click();

        // 모달이 열린 후 채팅방 열기
        setTimeout(() => {
            openChatRoomWithHighlight(chatRoomId, chatMessageId);
        }, 500);
    } else {
        openChatRoomWithHighlight(chatRoomId, chatMessageId);
    }
}

// 채팅방 열기 및 특정 메시지 하이라이트
function openChatRoomWithHighlight(chatRoomId, chatMessageId) {
    // 기존 openChatRoom 함수를 수정하여 사용
    AuthFunc.apiRequest(() =>
        axios.get(`${ctxPath}/api/chat/${chatRoomId}`, {
            headers: AuthFunc.getAuthHeader()
        })
    ).then(response => {
        const data = response.data.success.responseData;
        renderChatRoomModal(data);

        const bootstrapVersion = getBootstrapVersion();
        if (bootstrapVersion === 5) {
            const chatRoomModal = new bootstrap.Modal(document.getElementById("chatRoomModal"), {
                keyboard: true
            });
            chatRoomModal.show();

            // 모달이 표시된 후 메시지 하이라이트
            document.getElementById("chatRoomModal").addEventListener('shown.bs.modal', function () {
                setTimeout(() => {
                    highlightMessage(chatMessageId);
                }, 200);
            }, {once: true});
        } else if (bootstrapVersion === 4) {
            $('#chatRoomModal').modal('show');
            $('#chatRoomModal').on('shown.bs.modal', function () {
                setTimeout(() => {
                    highlightMessage(chatMessageId);
                }, 200);
            });
        }

        console.log(chatRoomId + ' 구독 시작');
        subscribeChatMessage(chatRoomId);

        // 룸 뱃지 없애기
        const roomLi = document.getElementById(chatRoomId);
        if (roomLi) {
            const badge = roomLi.querySelector('.unread-count-badge');
            if (badge) badge.remove();
        }
        updateTotalUnreadBadgeFromDOM();
    }).catch(error => {
        console.error("채팅방 불러오기 실패", error);
    });
}

// 특정 메시지 하이라이트 및 흔들림 애니메이션
function highlightMessage(chatMessageId) {
    const messageElement = document.getElementById(chatMessageId);
    if (!messageElement) return;

    // 메시지로 스크롤
    messageElement.scrollIntoView({
        behavior: 'smooth',
        block: 'center'
    });

    // 흔들림 애니메이션 추가
    setTimeout(() => {
        messageElement.classList.add('message-shake');

        // 애니메이션 완료 후 클래스 제거
        setTimeout(() => {
            messageElement.classList.remove('message-shake');
        }, 600);
    }, 500);
}
