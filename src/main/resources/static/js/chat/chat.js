// const apiRequest = AuthFunc.apiRequest;
// const authHeader = AuthFunc.getAuthHeader;

let stompClient = null;
let chatRoomId = null;
let userId = 1;


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
    const socket = new SockJS("/ws-chat");
    stompClient = Stomp.over(socket);

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
    const btnTalk = document.getElementById("btnTalk");
    const chatModal = new bootstrap.Modal(document.getElementById("chatModal"));
    const chatRoomList = document.getElementById("chatRoomList");

    btnTalk.addEventListener("click", function () {
        // 모달 열기
        chatModal.show();

        // 서버에서 채팅방 리스트 가져오기
        AuthFunc.apiRequest(() =>
            axios.get(`${ctxPath}/api/chat/rooms`, {
                headers: AuthFunc.getAuthHeader()
            })
        ).then(response => {
            console.log(response);
            const data = response.data.success.responseData;
            chatRoomList.innerHTML = ""; // 초기화

            data.forEach(room => {
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
                        `<span class="badge bg-danger">${room.unreadCount}</span>` :
                        ``}
        </div>
    </div>
`;

                li.querySelector(".room-list-right").addEventListener("click", () => {
                    // TODO: 채팅방 열기 로직 (예: /chat/room/${room.chatRoomId} 이동)
                    alert(room.chatRoomId + " 번 채팅방 열기!");
                });

                chatRoomList.appendChild(li);
            });
        })
            .catch(error => {
                console.error("채팅방 목록 불러오기 실패", error);
            });
    });
});

// 프로필 이미지를 클릭했을 때 원본 모달 표시
function openProfileImageModal(imgUrl) {
    const modalImg = document.getElementById("profileImageModalImg");
    modalImg.src = imgUrl;

    const profileModal = new bootstrap.Modal(document.getElementById("profileImageModal"));
    profileModal.show();
}




