const apiRequest = AuthFunc.apiRequest;
const authHeader = AuthFunc.getAuthHeader;

let stompClient = null;
let chatRoomId = null;
let userId = 1;


function connectRoom() {
    chatRoomId = document.getElementById("roomIdInput").value;
    console.log(chatRoomId);
    const socket = new SockJS("/ws-chat");
    stompClient = Stomp.over(socket);

    // jwt는 헤더에 Authorization: Bearer ... 로 붙음
    stompClient.connect({Authorization: authHeader().Authorization}, () => {
        stompClient.subscribe("/topic/" + chatRoomId, (message) => {
            const msg = JSON.parse(message.body);
            showMessage(msg);
        });
        alert("채팅방 " + chatRoomId + " 연결됨!");
    });
}

function send() {
    const content = document.getElementById("messageInput").value;
    if (!content) return;

    const requestBody = {
        chatRoomId: chatRoomId,
        content: content
    };

    // 👉 기존에 작성하신 apiRequest 래퍼 사용
    apiRequest(() =>
        axios.post("/api/chat/message",
            requestBody,
            {
                headers: authHeader()
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
    div.innerText = msg.senderId + ": " + msg.content;
    chatBox.appendChild(div);
    chatBox.scrollTop = chatBox.scrollHeight;
}